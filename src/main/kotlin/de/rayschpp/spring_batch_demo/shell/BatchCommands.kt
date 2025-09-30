package de.rayschpp.spring_batch_demo.shell

import org.slf4j.LoggerFactory
import org.springframework.batch.core.*
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Properties

@ShellComponent
class BatchCommands(
    private val jobOperator: JobOperator,
    private val jobExplorer: JobExplorer
) {
    private val log = LoggerFactory.getLogger(BatchCommands::class.java)

    @ShellMethod(key = ["batch:jobs", "jobs"], value = "List registered batch job names")
    fun listJobs(): String =
        jobOperator.jobNames.sorted().joinToString(separator = System.lineSeparator()) { it }
            .ifBlank { "<no jobs found>" }

    @ShellMethod(key = ["batch:start", "start"], value = "Start a job by name with optional parameters 'k=v,k2=v2'")
    fun startJob(
        @ShellOption(help = "Job name to start") jobName: String,
        @ShellOption(help = "Comma-separated job parameters 'k=v'", defaultValue = ShellOption.NULL) params: String?
    ): String {
        val properties = Properties()
        params?.takeIf { it.isNotBlank() }?.split(",")?.forEach { kv ->
            val idx = kv.indexOf('=')
            if (idx > 0 && idx < kv.length - 1) {
                val k = kv.substring(0, idx).trim()
                val v = kv.substring(idx + 1).trim()
                if (k.isNotEmpty()) properties[k] = v
            }
        }
        val id = try {
            jobOperator.start(jobName, properties)
        } catch (e: NoSuchJobException) {
            return "No such job: $jobName"
        }
        return "Started job '$jobName' with executionId=$id"
    }

    @ShellMethod(key = ["batch:instances", "instances"], value = "List last N job instances for a job")
    fun listInstances(
        @ShellOption(help = "Job name") jobName: String,
        @ShellOption(help = "How many to return", defaultValue = "10") count: Int
    ): String {
        val instances = jobExplorer.getJobInstances(jobName, 0, count)
        if (instances.isEmpty()) return "<no instances>"
        return instances.joinToString(System.lineSeparator()) { ji ->
            val last = jobExplorer.getJobExecutions(ji).maxByOrNull { it.startTime ?: it.createTime }
            "instanceId=${ji.instanceId} lastStatus=" + (last?.status ?: "-")
        }
    }

    @ShellMethod(key = ["batch:executions", "executions"], value = "List executions of a job instance")
    fun listExecutions(
        @ShellOption(help = "Job instance id") instanceId: Long
    ): String {
        val instance = jobExplorer.getJobInstance(instanceId) ?: return "No such instance: $instanceId"
        val executions = jobExplorer.getJobExecutions(instance)
        if (executions.isEmpty()) return "<no executions>"
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return executions.sortedBy { it.id }.joinToString(System.lineSeparator()) { e ->
            val start = e.startTime?.atZone(ZoneId.systemDefault())?.format(fmt)
            val end = e.endTime?.atZone(ZoneId.systemDefault())?.format(fmt)
            "executionId=${e.id} status=${e.status} exit=${e.exitStatus.exitCode} started=$start ended=$end"
        }
    }

    @ShellMethod(key = ["batch:last", "last"], value = "Show last execution for a job")
    fun lastExecution(
        @ShellOption(help = "Job name") jobName: String
    ): String {
        val running = jobOperator.getRunningExecutions(jobName).maxOrNull()?.let { jobExplorer.getJobExecution(it) }
        val last = running ?: run {
            val instances = jobExplorer.getJobInstances(jobName, 0, 1)
            if (instances.isEmpty()) return "No executions for $jobName"
            jobExplorer.getJobExecutions(instances.first()).maxByOrNull { it.startTime ?: it.createTime }
        }
        if (last == null) return "No executions for $jobName"
        return "executionId=${last.id} status=${last.status} exit=${last.exitStatus.exitCode}"
    }
}
