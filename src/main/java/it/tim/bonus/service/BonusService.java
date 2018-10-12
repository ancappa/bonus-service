package it.tim.bonus.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import it.tim.bonus.integration.client.BonusClient;
import it.tim.bonus.model.integration.AdjusteResponse;
import it.tim.bonus.model.web.BonusResponse;
import it.tim.bonus.model.exception.GenericException;
import it.tim.bonus.model.integration.*;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class BonusService {

    private static final DateTimeFormatter AUTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String OK_STATUS = "OK";
    private static final String KO_STATUS = "KO";
    private static final String DEBIT_TYPE = "CSO_002_014";
    private static final String BONUS_OK_CODE_1 ="1";
    private static final String BONUS_OK_CODE_19 ="19";
    private static final String ADJ_OP ="adjust";
    
    private BonusClient bonusClient;
    
    @Autowired
    public BonusService( BonusClient bonusClient) {
        this.bonusClient = bonusClient;
    }

    
    
    public AdjusteResponse manageAdjustBonus(String toMsisdn, String prize, String subSys, HttpHeaders headers){

    	AdjusteResponse resp = new AdjusteResponse();
    	
			
		LocalDateTime interactionDate = LocalDateTime.now();
        AdjustRequest adjusteReq = new AdjustRequest();
        adjusteReq.setMsisdn(toMsisdn);
        adjusteReq.setAmount(prize);
        adjusteReq.setSubSys(subSys);
        adjusteReq.setPaymentType(DEBIT_TYPE);
        adjusteReq.setInteractionDate(interactionDate.format(AUTH_DATE_FORMATTER));
        
        resp = AdjusteBonusMgt(adjusteReq,headers);
        
        return resp;
	    
    }
    
    public CommitAdjusteResponse manageCommitBonus(HttpHeaders headers){

    	CommitAdjusteResponse resp = new CommitAdjusteResponse();
    	
			
		LocalDateTime interactionDate = LocalDateTime.now();
        CommitAdjustRequest commitReq = new CommitAdjustRequest();
        commitReq.setInteractionDate(interactionDate.format(AUTH_DATE_FORMATTER));
        
        resp = CommitBonusMgt(commitReq,headers);
        
        return resp;
	    
    }

    
	private AdjusteResponse AdjusteBonusMgt(AdjustRequest adjRequest, HttpHeaders headers){
		
		AdjusteResponse resp = new AdjusteResponse();
		resp.setStatus(KO_STATUS);

		try {
			
			String adjReq = getAdjRequest(adjRequest, headers);
			log.info("------------------------------ ADJ REQ: " + adjReq);
			
			String adjResponse = bonusClient.callOBJ(adjReq);

			log.info("------------------------------ ADJ REPONSE: " + adjResponse);
			
			String esito = getTagValue(adjResponse, "ns:returnCode" , "-1");
			log.info("esito from BMV = " + esito);
			
			String description = getTagValue(adjResponse, "ns:returnDescription" , "-1");
			log.info("description from ADJ = " + description);
			
			if (esito!=null && (esito.equals(BONUS_OK_CODE_1) || esito.equals(BONUS_OK_CODE_19))) resp.setStatus(OK_STATUS);

			resp.setDescription(description);
			resp.setEsito(esito);
			resp.setInteractionDate(adjRequest.getInteractionDate());
			
			
	        
	        
			return resp;
		}
		catch(Exception ex) {
			log.error("ADJ EXC " + ex);
			throw new GenericException("Incomplete response reveived by service 'prepagatoMobile/offerte'");
		}

	}


	private String getAdjRequest(AdjustRequest adjRequest,HttpHeaders headers ) {
		
 
        
		StringBuilder buff = new StringBuilder();

		buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		buff.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://telecomitalia.it/SOA/SOAP/SOAPHeader\" xmlns:ns=\"http://telecomitalia.it/SOA/PrepaidMobileCreditMgmt/2015-05-11\" xmlns:ns1=\"http://telecomitalia.it/SOA/PrepaidMobileCreditMgmtCustomTypes/2015-05-11\"> ");
		buff.append("<soapenv:Header> ");
		buff.append("<soap:Header> ");
		buff.append("<soap:sourceSystem>").append(headers.getFirst("sourceSystem")).append("</soap:sourceSystem> ");
		buff.append("<soap:interactionDate> ");
		buff.append("<soap:Date>").append(adjRequest.getInteractionDate().substring(0, 10)).append("</soap:Date> ");
		buff.append("<soap:Time>").append(adjRequest.getInteractionDate().substring(11)).append("</soap:Time> ");
		buff.append("</soap:interactionDate> ");
		buff.append("<soap:businessID>").append(headers.getFirst("businessID")).append("</soap:businessID> ");
		buff.append("<soap:messageID>").append(headers.getFirst("messageID")).append("</soap:messageID> ");
		buff.append("<soap:transactionID>").append(headers.getFirst("transactionID")).append("</soap:transactionID> ");
		buff.append("</soap:Header> ");
		buff.append("</soapenv:Header> ");
		buff.append("<soapenv:Body> ");
		buff.append("<ns:adjustRequest> ");
		buff.append("<ns:ProductOrder> ");
		buff.append("<ns1:CustomerOrderItem> ");
		buff.append("<ns1:ProductBundle> ");
		buff.append("<ns1:ProductCharacteristicValue> ");
		buff.append("<ns1:value>").append(adjRequest.getMsisdn()).append("</ns1:value> ");
		buff.append("<ns1:ProductSpecCharacteristic> ");
		buff.append("<ns1:name>ServiceNumber</ns1:name> ");
		buff.append("</ns1:ProductSpecCharacteristic> ");
		buff.append("</ns1:ProductCharacteristicValue> ");
		buff.append("</ns1:ProductBundle> ");
		buff.append("<ns1:Product> ");
		buff.append("<ns1:CompositeProdPrice> ");
		buff.append("<ns1:ComponentProdPrice> ");
		buff.append("<ns1:price>").append(adjRequest.getAmount()).append("</ns1:price> ");
		buff.append("</ns1:ComponentProdPrice> ");
		buff.append("</ns1:CompositeProdPrice> ");
		buff.append("</ns1:Product> ");
		buff.append("<ns1:CharacteristicValue> ");
		buff.append("<ns1:value>").append(adjRequest.getSubSys()).append("</ns1:value> ");
		buff.append("<ns1:CharacteristicSpecification> ");
		buff.append("<ns1:name>Subsys</ns1:name> ");
		buff.append("</ns1:CharacteristicSpecification> ");
		buff.append("</ns1:CharacteristicValue> ");
		buff.append("<ns1:ProductComponent> ");
		buff.append("<ns1:ProductInvolvementRole> ");
		buff.append("<ns1:involvementRole>").append(adjRequest.getPaymentType()).append("</ns1:involvementRole> ");
		buff.append("<ns1:Customer> ");
		buff.append("<ns1:PaymentPlan> ");
		buff.append("<ns1:PaymentMethod> ");
		buff.append("<ns1:PaymentMethodType>0</ns1:PaymentMethodType> ");
		buff.append("</ns1:PaymentMethod> ");
		buff.append("</ns1:PaymentPlan> ");
		buff.append("</ns1:Customer> ");
		buff.append("</ns1:ProductInvolvementRole> ");
		buff.append("</ns1:ProductComponent> ");
		buff.append("</ns1:CustomerOrderItem> ");
		buff.append("<ns1:CharacteristicValue> ");
		buff.append("<ns1:value>1</ns1:value> ");
		buff.append("<ns1:CharacteristicSpecification> ");
		buff.append("<ns1:name>OperationCode</ns1:name> ");
		buff.append("</ns1:CharacteristicSpecification> ");
		buff.append("</ns1:CharacteristicValue> ");
		buff.append("</ns:ProductOrder> ");
		buff.append("<ns:ProcessData> ");
		buff.append("</ns:ProcessData> ");
		buff.append("</ns:adjustRequest> ");
		buff.append("</soapenv:Body> ");
	    buff.append("</soapenv:Envelope> ");

		return buff.toString();
	}
	
	private CommitAdjusteResponse CommitBonusMgt(CommitAdjustRequest cmtRequest, HttpHeaders headers){
		
		CommitAdjusteResponse resp = new CommitAdjusteResponse();
		resp.setStatus(KO_STATUS);

		try {
			
			String cmtReq = getCommitAdjRequest(cmtRequest, headers);
			log.info("------------------------------ CMT REQ: " + cmtReq);
			
			String cmtResponse = bonusClient.callOBJ(cmtReq);

			log.info("------------------------------ CMT REPONSE: " + cmtResponse);
			
			String esito = getTagValue(cmtResponse, "ns:returnCode" , "-1");
			log.info("esito from BMV = " + esito);
			
			String description = getTagValue(cmtResponse, "ns:returnDescription" , "-1");
			log.info("description from CMT = " + description);
			
			if (esito!=null && (esito.equals(BONUS_OK_CODE_1) || esito.equals(BONUS_OK_CODE_19))) resp.setStatus(OK_STATUS);

			resp.setDescription(description);
			resp.setEsito(esito);
			resp.setInteractionDate(cmtRequest.getInteractionDate());
			
			return resp;
		}
		catch(Exception ex) {
			log.error("CMT EXC " + ex);
			throw new GenericException("Incomplete response reveived by service 'prepagatoMobile/offerte'");
		}

	}

	private String getCommitAdjRequest(CommitAdjustRequest cmtRequest,HttpHeaders headers ) {
		
 
        
		StringBuilder buff = new StringBuilder();

		buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		buff.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://telecomitalia.it/SOA/SOAP/SOAPHeader\" xmlns:ns=\"http://telecomitalia.it/SOA/PrepaidMobileCreditMgmt/2015-05-11\" xmlns:ns1=\"http://telecomitalia.it/SOA/PrepaidMobileCreditMgmtCustomTypes/2015-05-11\"> ");
		buff.append("<soapenv:Header> ");
		buff.append("<soap:Header> ");
		buff.append("<soap:sourceSystem>").append(headers.getFirst("sourceSystem")).append("</soap:sourceSystem> ");
		buff.append("<soap:interactionDate> ");
		buff.append("<soap:Date>").append(cmtRequest.getInteractionDate().substring(0, 10)).append("</soap:Date> ");
		buff.append("<soap:Time>").append(cmtRequest.getInteractionDate().substring(11)).append("</soap:Time> ");
		buff.append("</soap:interactionDate> ");
		buff.append("<soap:businessID>").append(headers.getFirst("businessID")).append("</soap:businessID> ");
		buff.append("<soap:messageID>").append(headers.getFirst("messageID")).append("</soap:messageID> ");
		buff.append("<soap:transactionID>").append(headers.getFirst("transactionID")).append("</soap:transactionID> ");
		buff.append("</soap:Header> ");
		buff.append("</soapenv:Header> ");
		buff.append("<soapenv:Body> ");
		buff.append("<ns:commitAdjustRequest> ");
		buff.append("<ns:ProcessData> ");
		buff.append("<ns:Parameters> ");
		buff.append("<ns:Parameter> ");
		buff.append("<ns:name>Confirm</ns:name> ");
		buff.append("<ns:value>1</ns:value> ");
		buff.append("</ns:Parameter> ");
		buff.append("</ns:Parameters> ");
		buff.append("</ns:ProcessData> ");
		buff.append("</ns:commitAdjustRequest> ");
		buff.append("</soapenv:Body> ");
	    buff.append("</soapenv:Envelope> ");

		return buff.toString();
	}

	
	
	
	private static String getTagValue(String resp, String tag, String defaultVal ) {
		String tagValue = defaultVal;
		
		String tag1 = "<"+tag+">";
		String tag2 = "</"+tag+">";
		
		int idx1 = resp.indexOf(tag1);
		int idx2 = resp.indexOf(tag2);
		
		if(idx1>0 && idx2>0) {
			tagValue = resp.substring(idx1 + tag1.length(),idx2).trim();
		}
		
		return tagValue;
	}
	
	
	
	public static void main(String[] args) {
		String anno = "2021";
		String year = anno.substring(2);
		System.out.println("year = " + year);
		
		String objResp = "<TransactionType>PAGAM</TransactionType><TransactionResult>KO</TransactionResult><ShopTransactionID>1234</ShopTransactionID><BankTransactionID>5678900000</BankTransactionID><AuthorizationCode>"
				+ "</AuthorizationCode><Currency></Currency><Amount></Amount><Country></Country><Buyer><BuyerName></BuyerName><BuyerEmail></BuyerEmail></Buyer><CustomInfo></CustomInfo><ErrorCode>1125</ErrorCode><ErrorDescription>Anno di scadenza non valido</ErrorDescription><AlertCode></AlertCode><AlertDescription></AlertDescription><TransactionKey>196704321</TransactionKey><VbV><VbVFlag></VbVFlag><VbVBuyer>KO</VbVBuyer><VbVRisp></VbVRisp></VbV><TOKEN></TOKEN><TokenExpiryMonth></TokenExpiryMonth><TokenExpiryYear></TokenExpiryYear></GestPayS2S></callPagamS2SResult></callPagamS2SResponse></S:Body></S:Envelope>";

		
		String erroCode = getTagValue(objResp, "ErrorCode" , "-1");
		System.out.println("erroCode = " + erroCode);
		
		String shopTransactionID = getTagValue(objResp, "ShopTransactionID" , "");
		System.out.println("shopTransactionID = " + shopTransactionID);
		
		String bankTransactionID = getTagValue(objResp, "BankTransactionID" , "");
		System.out.println("bankTransactionID = " + bankTransactionID);
		
		LocalDateTime bankAuthDate = LocalDateTime.now();
		String authDate = bankAuthDate.format(AUTH_DATE_FORMATTER);
		
		System.out.println("authDate = " + authDate);
		
		

		String debit = "25.00000000000";
		int idx = debit.indexOf(".");
		String db1 = debit.substring(0,idx+3);
		System.out.println("db1= " + db1);
		
		
		UUID transaction = UUID.randomUUID();
    	String tid = transaction.toString();
    	System.out.println("tid="+tid);
		
    	SecureRandom random = new SecureRandom();
    	byte bytes[] = new byte[16];
    	random.nextBytes(bytes);
    	Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    	String token = encoder.encodeToString(bytes);
    	System.out.println(token);
		
	}
    
}
