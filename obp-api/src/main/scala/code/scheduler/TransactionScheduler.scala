package code.scheduler

import code.api.berlin.group.v1_3.model.TransactionStatus
import code.api.util.APIUtil
import code.transactionrequests.MappedTransactionRequest
import code.util.Helper.MdcLoggable
import net.liftweb.common.Full
import net.liftweb.mapper.{By, By_<}

import scala.util.{Failure, Success, Try}


object TransactionScheduler extends MdcLoggable {

  // Starts multiple scheduled tasks with different intervals
  def startAll(): Unit = {
    var initialDelay = 0

    // Berlin Group
    APIUtil.getPropsAsIntValue("berlin_group_outdated_transactions_interval_in_seconds") match {
      case Full(interval) if interval > 0 =>
        val time = APIUtil.getPropsAsIntValue("berlin_group_outdated_transactions_time_in_seconds", 300)
        SchedulerUtil.startTask(interval = interval, () => outdatedBerlinGroupTransactions(time)) // Runs periodically
        initialDelay = initialDelay + 10
      case _ =>
        logger.warn("|---> Skipping outdatedBerlinGroupTransactions task: berlin_group_outdated_transactions_interval_in_seconds not set or invalid")
    }
  }

  private def outdatedBerlinGroupTransactions(seconds: Int): Unit = {
    Try {
      logger.debug("|---> Checking for OUTDATED Berlin Group TRANSACTIONS...")

      val outdatedTransactions = MappedTransactionRequest.findAll(
        By(MappedTransactionRequest.mStatus, TransactionStatus.RCVD.toString),
        By_<(MappedTransactionRequest.updatedAt, SchedulerUtil.someSecondsAgo(seconds))
      )

      logger.debug(s"|---> Found ${outdatedTransactions.size} outdated transactions")

      outdatedTransactions.foreach { transaction =>
        Try {
          transaction.mStatus(TransactionStatus.RJCT.toString).save
          logger.warn(s"|---> Changed status to ${TransactionStatus.RJCT.toString} for transaction ID: ${transaction.id}")
        } match {
          case Failure(ex) => logger.error(s"Failed to update transaction ID: ${transaction.id}", ex)
          case Success(_) => // Already logged
        }
      }
    } match {
      case Failure(ex) => logger.error("Error in outdatedBerlinGroupTransactions!", ex)
      case Success(_) => logger.debug("|---> Task executed successfully")
    }
  }

}
