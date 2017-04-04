*	The standard Properties loader loads everything to a HashTable, so the order is lost for error reporting.
	Worse, there is record of line numbers for when a parse error happens, so the most specific we can be is
	just to say that an error happened in the file.  May need a new implementation of this.
*	If dynamic properties are supported, then there needs to be a way to do
	consistent reads.  Two ways to handle this:
	*	Get a read-key that would be associated w/ Value map.  Then have some way
		to use that read-key to read values, which would throw an exception if
		the read-key is invalid (i.e., the value map has been reassigned).
	*	Create a transaction object that would contain only the value map.
		All reads could be done against that transaction object.
		The trans object would share a ref to all of the values, so it would
		hold the values in memory if it did not close (typical issue w/ resources).
		*	The bet idiom for this would be a try (transaction = App.getTrans()) {}
		*	ConfigPointDynamic might be a base interface which requires a transcation
			to be passed in to get a value.  Only the more common non-dyn subclass
			would allow access to values w/o a transaction.
		*	Transactions that run over a minute of two should complain loudly.
	Both of those options involve isolating the value map.
*	Nulls might be explicitly set, possibly w/ EL.  In that case, the LoaderState
	(and ConfigPoints) will need to track if a value is explicitly set in some
	way other than figuring it out from a null value.
*	Add intentions to Loaders to indicate that a loader is intended to load
	an entire group.  This would require that all required params in a group
	must be present in via the loader.
*	Handle multi-values.