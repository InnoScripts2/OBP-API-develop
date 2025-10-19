package code.scheduler


import code.actorsystem.ObpLookupSystem

import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}
import scala.concurrent.duration._
object SchedulerUtil {

  private lazy val actorSystem = ObpLookupSystem.obpLookupSystem
  implicit lazy val executor = actorSystem.dispatcher
  private lazy val scheduler = actorSystem.scheduler

  // Generic method to schedule a task
  def startTask(interval: Long, task: () => Unit, initialDelay: Long = 0): Unit = {
    scheduler.schedule(
      initialDelay = Duration(initialDelay, TimeUnit.SECONDS),
      interval = Duration(interval, TimeUnit.SECONDS),
      runnable = new Runnable {
        def run(): Unit = task()
      }
    )
  }

  // Calculate the timestamp 5 minutes ago
  def someSecondsAgo(seconds: Int): Date = {
    val cal = Calendar.getInstance()
    cal.add(Calendar.SECOND, -seconds)
    cal.getTime
  }

}
