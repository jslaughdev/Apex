/*
 * Update Text Package field from opportunity look up value changes
 */
public class UpdatePackageOnAccounFromOpp
{
    Map<Id, Opportunity> oldOpps;
  	Map<Id, Opportunity> newOpps;
    
    
    public UpdatePackageOnAccount(Map<Id, Opportunity> oldTriggerOpps, Map<Id, Opportunity> newTriggerOpps) 
    {
      oldOpps = oldTriggerOpps;
      newOpps = newTriggerOpps;
  	}
   
    Set<Id> accountIds = new Set<Id>();
    Opportunity[] changes = new Opportunity[0];
    
  public void UpdateThePackage()
  {  
    Set<Id> accountIds = new Set<Id>();
    Opportunity[] changes = new Opportunity[0];
      
    for(Opportunity record: newOpps.values()) 
    {
        if(record.AccountId != null && (oldOpps.get(record.Id).package__c <> record.package__c)) 
        {
            changes.add(record);
            accountIds.add(record.AccountId);
        }
    }
    if(accountIds.isEmpty()) 
    {
        return;
    }
    Map<Id, Account> accounts = new Map<Id, Account>(
        [SELECT package__pc FROM Account WHERE Id = :accountIds]
    );
    for(Opportunity record: changes)
    {
       accounts.get(record.AccountId).package__pc =record.Fee_Plan_Enrollment__c;
    }
      update accounts.values();
  }
}
