# Service Provider Interfaces
Service Provider Interfaces (SPIs) provide a plugin style framework for developers to provide 
runtime-injected modules for search, data access, clustering and other services. 
[Avro](http://avro.apache.org/) IDL files in this directory are used to define the SPIs in 
language independent form.
The following is a log of changes to the avro service provider interfaces (SPIs) made with each version. 

## Version 1.7 Change Log

### Data Types
Changes to the `FL_DataTypes` protocol:
+ `FL_PropertyTag`
	+ added `CLUSTER`
+ `FL_Cluster`
	+ added `version`

### Clustering Data Access
Changes to the `FL_ClusteringDataAccess` protocol:
+ removed unused sessionId
+ added `getAccountOwners()`
+ `getContext()`
	+ Removed unused computeSummaries

### Clustering
Changes to the `FL_Clustering` protocol:
+ removed unused sessionId
+ removed unused `createContext()`
+ `clusterEntitiesById()`
	+ entityIds can contain uid's to entity clusters
	+ Added source and target context
+ `clusterEntities()`
	+ Added clusters to allow for clustering of entity clusters
	+ Added source and target context

### Search
Changes to the `FL_Search` protocol:
+ `FL_PropertyMatchDescriptor`
	+ reinstated `weight`
	+ added `similarity`
Changes to the `FL_EntitySearch` protocol:
+ `FL_PropertyDescriptor`
	+ added `defaultTerm` to indicate whether to include in the set of default criteria to specify
	+ added `freeTextIndexed` to indicate whether it is indexed for free text queries


	
## Version 1.6 Change Log

### Data Types 
Changes to the `FL_DataTypes` protocol:
+ `FL_PropertyTag`
	+ added `ENTITY_TYPE`
	+ added `ACCOUNT_OWNER`
	+ added `CLUSTER_SUMMARY`
	+ added `COUNTRY_CODE`
+ `FL_Uncertainty`
	+ added data `currency` as an optional indicator of degrading confidence over time
+ `FL_ContinentCode`
	+ added in 1.6. enum member of `FL_Country`
+ `FL_Country`
	+ added in 1.6
+ `FL_Frequency`
	+ added in 1.6. member of `FL_DistributionRange`
+ `FL_DistributionRange`
	+ added in 1.6
+ `FL_RangeType`
	+ added `DISTRIBUTION`
+ `FL_Property`
	+ added `FL_DistributionRange` as an option for member range
+ `FL_EntityTag`
	+ added `ACCOUNT_OWNER` and `CLUSTER_SUMMARY`

### Data Access
Changes to the `FL_DataAccess` protocol:
+ added `FL_LinkEntityTypeFilter`
+ added `entityType` filter parameter to `getFlowAggregation()`
+ `getAllTransactions()`
	+ changed return type to new `FL_TransactionResults` which include total result count
	+ added a `start` index
+ added `FL_LevelOfDetail` to `getEntities()` call to provide distinguish requests for summary attributes from requests for full entity details.

### Clustering Data Access
Changes to the `FL_ClusteringDataAccess` protocol:
+ add `getClusterSummary()` method
+ removed `getAccounts()` method
+ renamed `getEntities()` to `getClusters()`
		
## Version 1.5

### Data Types 
Changes to the `FL_DataTypes` protocol:
+ `FL_PropertyTag`
	+ `CREDIT`/`DEBIT` changed to `INFLOWING`/`OUTFLOWING`
	+ added `USD`
	+ added `DURATION`
+ `FL_PropertyType`
	+ removed `SERIES` (use the new `FL_RangeType` to define a Range)
+ Series
    + use the `FL_RangeType` to define a Range
+ `FL_DateInterval`
    + moved here from DataAccess/ClusteringDataAccess
	+ added `SECONDS` and `HOURS`
	+ renamed `DAILY`/`WEEKLY`/`MONTHLY`/`QUARTERLY`/`YEARLY` to `DAYS`/`WEEKS`/`MONTHS`/`QUARTERS`/`YEARS`
+ `FL_Duration`
	+ added in 1.5
+ `FL_DateRange`
	+ removed interval and numIntervalsPerBin and replaced with durationPerBin
+ `FL_SingletonRange`, `FL_ListRange`, `FL_BoundedRange`, `FL_RangeType`
	+ added in 1.5
+ `FL_Property`
	+ value can now be either a `FL_SingletonRange`, `FL_ListRange` or `FL_BoundedRange`

### Data Access
Changes to the `FL_DataAccess` protocol:
+ removed `FL_DateInterval`, `FL_DateRange` in favor of common definitions in DataTypes

### Clustering Data Access
Changes to the `FL_ClusteringDataAccess` protocol:
+ removed `FL_DateInterval`, `FL_DateRange` in favor of common definitions in DataTypes

### Search
Changes to the `FL_Search` protocol:
+ `FL_Constraint`
	+ removed range-based constraints (use a `FL_BoundedRange` instead)
	+ renamed presence-based constraints to reflect various use cases
+ `FL_PropertyMatchDescriptor`
	+ changed `value` to a `FL_Range`
	+ changed `relative` to `variable`
	+ added boolean `include`
	+ changed `weight` to `FL_Constraint`

### Entity Search
Changes to the `FL_EntitySearch` protocol:
+ `FL_PropertyDescriptor`
	+ added optional `FL_RangeType`
	+ renamed optional `FL_Constraint` to constraint
+ `search()`
	+ added `type`, passed in based on the selection from `getDescriptors()`

### Pattern Search
Changes to the `FL_PatternSearch` protocol:
+ `FL_EntityMatchDescriptor`
	+ removed `sameAs`
	+ replaced `weight` with `FL_Constraint`
+ `FL_PathMatchTag`
	+ added in 1.5
+ `FL_LinkMatchDescriptor`
	+ removed `FL_PathMatchDescriptor` (use `FL_PropertyMatchDescriptor`s with `FL_PathMatchTag`s)
	+ replaced `weight` with `FL_Constraint`
+ `searchByExample()`, `searchByTemplate()`
    + added optional `dateRange` filter to search calls