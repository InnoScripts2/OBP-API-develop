package code.transaction.internalMapping

import com.openbankproject.commons.model.{BankId, TransactionId}

/**
 * This trait is used for storing the mapped between obp transaction_id and bank real transaction reference.
 * transactionPlainTextReference is just a plain text from bank. Bank need prepare it and make it unique for each Transaction.
 *
 * eg: Once we create the transaction over CBS, we need also create a TransactionId in api side.
 *     For security reason, we can only use the transactionId (UUID) in the apis.  
 *     Because these id’s might be cached on the internet.
 */
trait TransactionIdMappingTrait {
 
  def transactionId : TransactionId
  /**
   * This is the bank transaction plain text string, need to be unique for each transaction. ( Bank need to take care of it)
   * @return  It can be concatenated of real bank transaction data: eg: transactionPlainTextReference =  transactionNumber + transactionCode + transactionType
   */
  def transactionPlainTextReference : String
}