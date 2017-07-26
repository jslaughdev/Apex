/*
 * Block comments with details of changes
 */
public class HandleFeeTrigger
{

public static boolean alreadyProcessed =false;

    public static void handlefeecodes(List<opportunity> opplist, Map<id,opportunity> oldmap, boolean isUpdate)
	{
		if(alreadyProcessed) return;
		alreadyProcessed = true;

		for(opportunity opp : opplist)
		{
			if(isUpdate &&
				opp.FundsAvailable__c != oldMap.get(opp.id).FundsAvailable__c &&opp.FundsAvailable__c >= opp.FeeAmounttoRequest__c)
          
				opp.sendfeecodes__c = true;
				AttemptRequestAsync();
           
		}
	} 

	public static void AttemptRequestAsync()
		{
			if(system.isFuture() || system.isBatch())
			{
				AttemptAsync();
			}
			else
			{
				if(system.Limits.getFutureCalls()<
					system.Limits.getLimitFutureCalls() - 4)
				
					AttemptAsync();
				
			}
		}

		@future(callout=true)
		private static void AttemptAsync()
		{
			AttemptSync();
		}

		public static void AttemptSync()
		{
			Integer AllowedCallouts =
				Limits.getLimitCallouts() - Limits.getCallouts();
			if(allowedCallouts <=0) return;
			List<opportunity>OppsForFees = 
				[SELECT ID, sendfeecodes__c from opportunity
				WHERE LastModifiedDate > :DateTime.Now().addHours(-24)
				AND sendfeecodes__c = true
				LIMIT :system.Limits.getLimitCallouts()];
			for(opportunity opp : OppsForFees)
			{
      				  AMP_Fee_HTTP.process(opp.id);
                opp.sendfeecodes__c = false;
			}
			update oppsforfees;
		}
	}
