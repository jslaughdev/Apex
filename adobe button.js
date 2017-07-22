{!REQUIRESCRIPT( "/soap/ajax/36.0/connection.js" )} 
{!REQUIRESCRIPT( "/soap/ajax/36.0/apex.js" )} 

var MasterObjectId = document.URL.split("/")[3].split("?")[0]; 


var MasterObjectType; 

switch ( MasterObjectId.substring(0, 3) ) { 
case '001': MasterObjectType = 'Account'; break; 
case '003': MasterObjectType = 'Contact'; break; 
case '005': MasterObjectType = 'User'; break; 
case '006': MasterObjectType = 'Opportunity'; break; 
case '800': MasterObjectType = 'Contract'; break; 
case '500': MasterObjectType = 'Case'; break; 
case '00Q': MasterObjectType = 'Lead'; break; 

default: MasterObjectType = "Lead"; 
} 

var pkg = '{!Lead.Ameritech_Package_Name__c}'; 
var st = '{!Lead.State}'; 
var pp = '{!Lead.paymentprocessor__c}'; 

var id = sforce.apex.execute("contract_selector","thequery",{pkgname:pkg,state:st,pprocessor:pp}); 

if(id.length > 0) 
{ 
location.href = '/apex/echosign_dev1__AgreementTemplateProcess?masterId=' + MasterObjectId + '&TemplateId=' + id; 
} 

else if( records.length == 0) { 

alert('There are no agreement templates to choose from.'); 

} 
else{ 

var agreements = sforce.connection.query( "SELECT Id, Name FROM echosign_dev1__Agreement_Template__c WHERE echosign_dev1__Master_Object_Type__c = '" + MasterObjectType + "' ORDER BY Name ASC" ); 

records = agreements.getArray( "records" ); 

if( records.length == 0) { 

alert('There are no agreement templates to choose from.'); 

} 

var agreementListHtml = '<br />'; 
for (var i=0; i< records.length; i++) { 

agreementListHtml += '&nbsp;&nbsp;&nbsp;<h3><a href="/apex/echosign_dev1__AgreementTemplateProcess?masterId=' + MasterObjectId + '&TemplateId=' + records[i].Id + '" title="' + records[i].Id + '">' + records[i].Name + '</a></h3>'; 

if( i< records.length ) 
{ 

agreementListHtml += '<br /><br />'; 
} 

} 

var closeButtonHtml = "<p style='text-align:right;'><button class='btn' onclick='window.parent.sd.hide(); return false;'>&nbsp;Close&nbsp;</button>&nbsp;&nbsp;&nbsp;</p>"; 
var sd = new SimpleDialog( "w" + Math.random(), false ); 

sd.setTitle( "Choose an Agreement Template" ); 
sd.createDialog(); 
window.parent.sd = sd; 
sd.setContentInnerHTML( agreementListHtml + closeButtonHtml ); 
sd.show(); 

}