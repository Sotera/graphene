# Service Provider Interfaces
Service Provider Interfaces (SPIs) provide a plugin style framework for developers to provide 
runtime-injected modules for search, data access, clustering and other services. 
[Avro](http://avro.apache.org/) IDL files in this directory are used to define the SPIs in 
language independent form.
The following is a log of changes to the avro service provider interfaces (SPIs) made with each version. 

## Version 1.8 Change Log

### Search
Changes to `G_Search` protocol:
+ added `G_TypeDescriptor` for defining types that properties belong to, replacing a simple string type name.
+ moved 'G_PropertyDescriptor' from G_EntitySearch to G_Search.
+ added `G_TypeMapping` for defining how G_PropertyDescriptors map to types.
+ each `G_PropertyDescriptor` can now list one or more G_TypeMappings for each type that it belongs to and what field it represents.
+ the data `type` field of `G_PropertyDescriptor` was renamed to `propertyType` for clarity.
+ G_PropertyMatchDescriptor now accepts an array of G_TypeMappings as types and fields to match against.

Changes to 'G_EntitySearch' protocol:
+ added G_PropertyDescriptors to contain arrays of Property and Type descriptors
+ `getDescriptors()` now returns an instance of G_PropertyDescriptors rather than a map of type to list.
+ G_PropertyDescriptor was moved to G_Search. See above.
 
### Bug Fixes
When defining an Avro union with a `null` default value it must be defined as `union{null,string}=null`
and not `union{string,null}=null` or Avro throws a wobbly when the value is null.

## Version 1.7 Change Log

### Data Types
Changes to the `G_DataTypes` protocol:
+ `G_PropertyTag`
	+ added `CLUSTER`
+ `G_Cluster`
	+ added `version`

### Clustering Data Access
Changes to the `G_ClusteringDataAccess` protocol:
+ removed unused sessionId
+ added `getAccountOwners()`
+ `getContext()`
	+ Removed unused computeSummaries

### Clustering
Changes to the `G_Clustering` protocol:
+ removed unused sessionId
+ removed unused `createContext()`
+ `clusterEntitiesById()`
	+ entityIds can contain uid's to entity clusters
	+ Added source and target context
+ `clusterEntities()`
	+ Added clusters to allow for clustering of entity clusters
	+ Added source and target context

### Search
Changes to the `G_Search` protocol:
+ `G_PropertyMatchDescriptor`
	+ reinstated `weight`
	+ added `similarity`
Changes to the `G_EntitySearch` protocol:
+ `G_PropertyDescriptor`
	+ added `defaultTerm` to indicate whether to include in the set of default criteria to specify
	+ added `freeTextIndexed` to indicate whether it is indexed for free text queries


	
## Version 1.6 Change Log

### Data Types 
Changes to the `G_DataTypes` protocol:
+ `G_PropertyTag`
	+ added `ENTITY_TYPE`
	+ added `ACCOUNT_OWNER`
	+ added `CLUSTER_SUMMARY`
	+ added `COUNTRY_CODE`
+ `G_Uncertainty`
	+ added data `currency` as an optional indicator of degrading confidence over time
+ `G_ContinentCode`
	+ added in 1.6. enum member of `G_Country`
+ `G_Country`
	+ added in 1.6
+ `G_Frequency`
	+ added in 1.6. member of `G_DistributionRange`
+ `G_DistributionRange`
	+ added in 1.6
+ `G_RangeType`
	+ added `DISTRIBUTION`
+ `G_Property`
	+ added `G_DistributionRange` as an option for member range
+ `G_EntityTag`
	+ added `ACCOUNT_OWNER` and `CLUSTER_SUMMARY`

### Data Access
Changes to the `G_DataAccess` protocol:
+ added `G_LinkEntityTypeFilter`
+ added `entityType` filter parameter to `getFlowAggregation()`
+ `getAllTransactions()`
	+ changed return type to new `G_TransactionResults` which include total result count
	+ added a `start` index
+ added `G_LevelOfDetail` to `getEntities()` call to provide distinguish requests for summary attributes from requests for full entity details.

### Clustering Data Access
Changes to the `G_ClusteringDataAccess` protocol:
+ add `getClusterSummary()` method
+ removed `getAccounts()` method
+ renamed `getEntities()` to `getClusters()`
		
## Version 1.5

### Data Types 
Changes to the `G_DataTypes` protocol:
+ `G_PropertyTag`
	+ `CREDIT`/`DEBIT` changed to `INFLOWING`/`OUTFLOWING`
	+ added `USD`
	+ added `DURATION`
+ `G_PropertyType`
	+ removed `SERIES` (use the new `G_RangeType` to define a Range)
+ Series
    + use the `G_RangeType` to define a Range
+ `G_DateInterval`
    + moved here from DataAccess/ClusteringDataAccess
	+ added `SECONDS` and `HOURS`
	+ renamed `DAILY`/`WEEKLY`/`MONTHLY`/`QUARTERLY`/`YEARLY` to `DAYS`/`WEEKS`/`MONTHS`/`QUARTERS`/`YEARS`
+ `G_Duration`
	+ added in 1.5
+ `G_DateRange`
	+ removed interval and numIntervalsPerBin and replaced with durationPerBin
+ `G_SingletonRange`, `G_ListRange`, `G_BoundedRange`, `G_RangeType`
	+ added in 1.5
+ `G_Property`
	+ value can now be either a `G_SingletonRange`, `G_ListRange` or `G_BoundedRange`

### Data Access
Changes to the `G_DataAccess` protocol:
+ removed `G_DateInterval`, `G_DateRange` in favor of common definitions in DataTypes

### Clustering Data Access
Changes to the `G_ClusteringDataAccess` protocol:
+ removed `G_DateInterval`, `G_DateRange` in favor of common definitions in DataTypes

### Search
Changes to the `G_Search` protocol:
+ `G_Constraint`
	+ removed range-based constraints (use a `G_BoundedRange` instead)
	+ renamed presence-based constraints to reflect various use cases
+ `G_PropertyMatchDescriptor`
	+ changed `value` to a `G_Range`
	+ changed `relative` to `variable`
	+ added boolean `include`
	+ changed `weight` to `G_Constraint`

### Entity Search
Changes to the `G_EntitySearch` protocol:
+ `G_PropertyDescriptor`
	+ added optional `G_RangeType`
	+ renamed optional `G_Constraint` to constraint
+ `search()`
	+ added `type`, passed in based on the selection from `getDescriptors()`

### Pattern Search
Changes to the `G_PatternSearch` protocol:
+ `G_EntityMatchDescriptor`
	+ removed `sameAs`
	+ replaced `weight` with `G_Constraint`
+ `G_PathMatchTag`
	+ added in 1.5
+ `G_LinkMatchDescriptor`
	+ removed `G_PathMatchDescriptor` (use `G_PropertyMatchDescriptor`s with `G_PathMatchTag`s)
	+ replaced `weight` with `G_Constraint`
+ `searchByExample()`, `searchByTemplate()`
    + added optional `dateRange` filter to search calls