global class ProcessInboundEmail implements Messaging.InboundEmailHandler {

  global Messaging.InboundEmailResult handleInboundEmail(Messaging.InboundEmail email,
    Messaging.InboundEnvelope envelope) {

    Messaging.InboundEmailResult result = new Messaging.InboundEmailresult();

    Contact c = new Contact();
    c.FirstName = email.fromname.substring(0,email.fromname.indexOf(' '));
    c.LastName = email.fromname.substring(email.fromname.indexOf(' '));
    c.Email = envelope.fromAddress;
    insert contact;

    System.debug('====> Created contact '+c.Id);

    //
    /*
    lead l = new lead();
    l.FirstName = = email.fromname.substring(0,email.fromname.indexOf(' '));
    l.LastName = email.fromname.substring(email.fromname.indexOf(' '));
    l.Email = envelope.fromAddress;
    lleadsource = 'Inbound Email lead';
    insert contact;

    System.debug('====> Created lead '+l.Id);
    */

    if (email.binaryAttachments != null && email.binaryAttachments.size() > 0) {
      for (integer i = 0 ; i < email.binaryAttachments.size() ; i++) {
        Attachment attachment = new Attachment();
        // attach to the newly created contact record
        attachment.ParentId = c.Id;
        attachment.Name = email.binaryAttachments[i].filename;
        attachment.Body = email.binaryAttachments[i].body;
        insert attachment;
      }
    }

    return result;

  }

}
