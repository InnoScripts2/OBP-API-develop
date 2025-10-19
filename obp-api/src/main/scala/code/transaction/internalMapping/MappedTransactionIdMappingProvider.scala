package code.transaction.internalMapping

import code.util.Helper.MdcLoggable
import com.openbankproject.commons.model.TransactionId
import net.liftweb.common._
import net.liftweb.mapper.By


object MappedTransactionIdMappingProvider extends TransactionIdMappingProvider with MdcLoggable
{

  override def getOrCreateTransactionId(
    transactionPlainTextReference: String
  ) =
  {

    val transactionIdMapping = TransactionIdMapping.find(
      By(TransactionIdMapping.TransactionPlainTextReference, transactionPlainTextReference)
    )

    transactionIdMapping match
    {
      case Full(vImpl) =>
      {
        logger.debug(s"getOrCreateTransactionId --> the TransactionIdMapping has been existing in server !")
        transactionIdMapping.map(_.transactionId)
      }
      case Empty =>
      {
        val transactionIdMapping: TransactionIdMapping =
          TransactionIdMapping
            .create
            .TransactionPlainTextReference(transactionPlainTextReference)
            .saveMe
        logger.debug(s"getOrCreateTransactionId--> create mappedTransactionIdMapping : $transactionIdMapping")
        Full(transactionIdMapping.transactionId)
      }
      case Failure(msg, t, c) => Failure(msg, t, c)
      case ParamFailure(x,y,z,q) => ParamFailure(x,y,z,q)
    }
  }


  override def getTransactionPlainTextReference(transactionId: TransactionId) = {
    TransactionIdMapping.find(
      By(TransactionIdMapping.TransactionId, transactionId.value),
    ).map(_.transactionPlainTextReference)
  }
}

