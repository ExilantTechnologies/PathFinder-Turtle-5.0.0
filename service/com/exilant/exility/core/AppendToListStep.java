package com.exilant.exility.core;

class AppendToListStep extends AbstractStep
{
	String name = null;
	//looks like this is an error. This is no different from executeOnCondition 
	Expression condition = null;
	Expression expression = null;
	
	AppendToListStep()
	{
		this.stepType = StepType.APPENDTOLISTSTEP;
	}

	@Override
	String executeStep(DataCollection dc, DbHandle handle) throws ExilityException
	{
		if(this.name == null || this.name.equals(""))
		{
			dc.addMessage("exilityError", "The variable name not specified to create the list");
			return AbstractStep.NEXT;
		}
		
		if ((this.condition != null) && (!this.condition.toString().equals("")))
			if (!this.condition.evaluate(dc).getBooleanValue())
				return AbstractStep.NEXT;
		
		ValueList list = dc.getValueList(this.name);
		if(list == null)
		{
			list = new ValueList(1);
		}
		else
		{
			ValueList templist = new ValueList(list.length() + 1);
			for(int i = 0; i < list.length(); i++)
			{
				templist.setValue(list.getValue(i), i);
			}
			list = templist;
		}
		
		Value val = this.expression.evaluate(dc);
		list.setValue(val, list.length() - 1);
		
		dc.addValueList(this.name, list);
		
		return AbstractStep.NEXT;
	}

}