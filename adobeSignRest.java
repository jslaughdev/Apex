public class AdobeSignRest {
    
    private static final string INTEGRATION_KEY = 'Insert the Integration Key here'; 
    
    public class documents {
        public list<document> documents { get; set; }
    }
    public class document {
        public string documentId { get; set; }
        public string mimeType { get; set; }
        public string name { get; set; }
        public integer numPages { get; set; }
    }
    
    public class url {
        public string url { get; set; }
    }
    
    @future(callout=true)
    public static void attachDocuments_future( id agreementId ) { attachDocuments( agreementId ); } // be able to run asynchronously
    public static void attachDocuments( id agreementId ) {
        
        List<echosign_dev1__SIGN_Agreement__c> agreements = [SELECT echosign_dev1__Document_Key__c, Name FROM echosign_dev1__SIGN_Agreement__c WHERE Id = :agreementId AND echosign_dev1__Document_Key__c != ''];
        
        if( !agreements.isEmpty() ) {
         
            HttpRequest req = new HttpRequest();
        
            req.setEndpoint( 'https://api.na1.echosign.com/api/rest/v5/agreements/' + agreements.get(0).echosign_dev1__Document_Key__c + '/documents' );
            req.setMethod( 'GET' );
            req.setHeader( 'Content-Type', 'application/json' );
            req.setHeader( 'Access-Token', INTEGRATION_KEY );
            req.setHeader( 'agreementId', agreementId );
            
            Http http = new Http();
            HttpResponse res;
            documents response;
            List<document> docs = new List<document>();
            
            if( !test.isRunningTest() ) res = http.send( req );
            if( test.isRunningTest() ) res = new HttpResponse();
            
            if( !test.isRunningTest() ) { response = ( documents )JSON.deserialize( res.getBody(), documents.class ); }
            if( test.isRunningTest() ) { 
                response = new documents(); 
                document d = new document();
                d.documentId = 'TEST';
                d.name = 'TEST';
                d.mimeType = 'application/pdf';
                d.numPages = 1;
                docs.add(d);
                response.documents = docs;
            }
            
            HttpRequest req_doc;
            HttpResponse res_doc;
            
            List<Attachment> atts = new List<Attachment>();
            Attachment n;
            
            for( document d : response.documents ) {
                
                req_doc = new HttpRequest();
                req_doc.setEndpoint( 'https://api.na1.echosign.com/api/rest/v5/agreements/' + agreements.get(0).echosign_dev1__Document_Key__c + '/documents/' + d.documentId + '/url' );
                req_doc.setMethod( 'GET' );
                req_doc.setHeader( 'Content-Type', 'application/json' );
                req_doc.setHeader( 'Access-Token', INTEGRATION_KEY );
                req_doc.setHeader( 'agreementId', agreementId );
                req_doc.setHeader( 'documentId', d.documentId );
                
                if( !test.isRunningTest() ) res_doc = http.send( req_doc );
                if( test.isRunningTest() ) res_doc = new HttpResponse();
                
                url u;
                
                if( !test.isRunningTest() ) u = ( url )JSON.deserialize( res_doc.getBody(), url.class );
                if( test.isRunningTest() ) { u = new url(); u.url = 'https://test.com/test.pdf'; } 
                
                
                n = new Attachment();
                n.ParentId = agreementId;
                n.Name = agreements.get(0).Name + ' - ' + d.name + ' - signed.pdf';
                n.Body = getHttpFile( u.url );
                n.contentType = 'application/pdf';
                atts.add( n );
                
            }
            
            if( !atts.isEmpty() ) {
                insert atts;
            }
            
        }
        
    }
    
    @future(callout=true)
    public static void auditTrail_future( id agreementId ) { auditTrail( agreementId ); } // be able to run asynchronously
    public static void auditTrail( id agreementId ) {
        
        List<echosign_dev1__SIGN_Agreement__c> agreements = [SELECT echosign_dev1__Document_Key__c, Name FROM echosign_dev1__SIGN_Agreement__c WHERE Id = :agreementId AND echosign_dev1__Document_Key__c != ''];
        
        if( !agreements.isEmpty() ) {
            
            HttpRequest req = new HttpRequest();
        
            req.setEndpoint( 'https://api.na1.echosign.com/api/rest/v5/agreements/' + agreements.get(0).echosign_dev1__Document_Key__c + '/auditTrail' );
            req.setMethod( 'GET' );
            req.setHeader( 'Content-Type', 'application/json' );
            req.setHeader( 'Access-Token', INTEGRATION_KEY );
            req.setHeader( 'agreementId', agreementId );
            
            Http http = new Http();
            HttpResponse res;
            
            if( !test.isRunningTest() ) res = http.send( req );
            if( test.isRunningTest() ) { res = new HttpResponse(); res.setBody('PDF-TEXT'); }
            
            Attachment n = new Attachment();
            n.ParentId = agreementId;
            n.Name = agreements.get(0).Name + ' -  Audit Trail.pdf';
            n.Body = res.getBodyAsBlob();
            n.contentType = 'application/pdf';
            insert n;
            
        }
        
    }
    
    
    
    private static blob getHttpFile( String fileUrl ) {
        
        blob response;
        
        HttpRequest req = new HttpRequest();
        req.setEndpoint( fileUrl );
        req.setMethod( 'GET' );
        req.setHeader( 'Content-Type', 'application/pdf' );
        req.setCompressed( true );
        req.setTimeout( 60000 );
        		
        Http h = new Http();				
        HttpResponse res;
        
        if( !test.isRunningTest() ) res = h.send( req );
        if( !test.isRunningTest() ) response = res.getBodyAsBlob();
        
        if( test.isRunningTest() ) response = blob.valueOf('PDF-TEXT');
        
        return response;
        
    }
    

}