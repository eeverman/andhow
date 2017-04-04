Not having to worry about property names is nice for new applications, but what about existing applications that rely on finding specifically named properties?

AndHow uses _alias_ and _exports_ to bridge the gap between legacy 'name dependent' applications and its name insensitive approach.  Below is an example `PropertyGroup` that might be used for a legacy application expecting to find its configuration in System.Properties with a specific set of names:
```java
@GroupExport(
  exporter=SysPropExporter.class,
  exportByCanonicalName=Exporter.EXPORT_CANONICAL_NAME.NEVER,
  exportByOutAliases=Exporter.EXPORT_OUT_ALIASES.ALWAYS
)
public interface ShoppingCartSvsConfig extends PropertyGroup {
  StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").aliasInAndOut("cart.svs").build();
  IntProp TIMEOUT = IntProp.builder().aliasInAndOut("cart.to").aliasOut("timeout").build();
  StrProp QUERY_ENDPOINT = StrProp.builder().aliasInAndOut("cart.query").build();
  StrProp ITEM_ENDPOINT = StrProp.builder().required().aliasInAndOut("cart.item").build();
}
```
Each property is given an 'InAndOut' alias.  An _in_ alias is just an added name that will be recognized when reading property values from some configuration source, like JNDI or System.Properties.  An _out_ alias is available for use when the property names and values are exported to some configuration destination, like JNDI  or System.Properties.  

Exports are configured by adding a GroupExport annotation to a `PropertyGroup` and happen as soon as all property loaders have completed.  In this example, the annotation specifies the `SysPropExporter`, which exports properties to System.Properties.  It also specifies that the canonical names of the properties not be used (the legacy code wouldn't recognize those names) and that all properties with _out_ aliases should be included in the export.

By using `aliasInAndOut` the application can keep its existing configuration source with the existing names (perhaps it was reading from a Properties file) and still benefit from validation checks, type checks, fast fail, and other features of AndHow.  If you later want to migrate to a new configuration source, such as Environmental variables, you can do that and still allow your code to read the properties from System.Properties via exports.

Further, multiple `aliasInAndOut`, `aliasIn` and `aliasOut`'s can be added to a single property.  Say you discover that your legacy code has been using two separately named configuration properties for TIMEOUT.  In the example above, that property was given an extra out alias so that any legacy code reading `cart.to` or `timeout` from System.Properties will find the same value, populated from a single configuration point.
