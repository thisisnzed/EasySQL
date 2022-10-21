# Description

A framework **based** on mysql-connector to **simplify** the usage of **databases, tables & columns**.
This framework **automatically** creates the **databases** & **tables** if they do not already exist ; so you don't have to create them manually.

# Developers

You can reuse EasySQL but make sure you comply with the [LICENSE](https://github.com/thisisnzed/EasySQL/blob/main/LICENSE).

## Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.easysql</groupId>
        <artifactId>EasySQL</artifactId>
        <version>1.0.3</version>
    </dependency>
</dependencies>
```

# Usage

⚠️ **THERE ARE NOT ALL THE METHODS HERE. THESE ARE ONLY EXAMPLES.**

## Simple configure connection & database

```java
final EasySQL easySQL = new EasySQL();
easySQL.setHost("127.0.0.1");
easySQL.setPort(3306);
easySQL.setUser("root");
easySQL.setPassword("password");
easySQL.setDatabase("demo");
```

## Add specific tables

```java
// First table
final Table playerTable = new Table("players");
playerTable.setPrimaryKey("uuid");
playerTable.addColumn("uuid", "VARCHAR(36)");
playerTable.addColumn("kills", "INTEGER");
playerTable.addColumn("deaths", "INTEGER");

// Second table
final Table demoTable = new Table("demo2");
demoTable.setPrimaryKey("uuid");
demoTable.addColumn("uuid", "VARCHAR(36)");
demoTable.addColumn("totalConnections", "INTEGER");

// Create the tables if they do not exist
easySQL.createDefaultTables(playerTable, demoTable); //Here you can enter the number of tables you want 
```
As you can see above, you can use "int" instead of "INTEGER", "string" instead of "VARCHAR(255)", "double" instead of "DOUBLE" and more. 
But you can also insert the default types like "INTEGER", "VARCHAR(36)"...

## Establish connection

```java
easySQL.connect();
```

## Close connection

```java
easySQL.close();
```

## Delete database

```java
easySQL.delete();
```

## Delete table

```java
playerTable.delete();
```

## Insert default VALUES

```java
final Column column = playerTable.getColumns();
column.insertDefault("cdb4810e-b975-4fbd-97be-3aa838a017aa", 0, 0); //uuid, kills, deaths --> see above to understand the order of values
```

## Edit values (replacement)

```java
column.editValue("uuid", "cdb4810e-b975-4fbd-97be-3aa838a017aa", "kills", 2)
```

## Check if value exists in specific column

```java
if(column.isExists("uuid", "cdb4810e-b975-4fbd-97be-3aa838a017aa")) {
  System.out.println("Yes !");
} else {
  System.out.println("No !");
}
```

## Getting value

```java
System.out.println("Kills : " + column.getValue("uuid", "cdb4810e-b975-4fbd-97be-3aa838a017aa", "kills"));
```

Returning `Kills : 2`

## Get table name

```java
System.out.println("Table name : " + column.getTableName());
```

Returning `Table name : players`

## Remove row in column

```java
column.delete("uuid", "cdb4810e-b975-4fbd-97be-3aa838a017aa");
```

## Simple example for bukkit
In this example, if the player who connects does not have a profile in the database, plugin will create one for him.

Then, for each broken block, add 1 to "blocks" in database and print that value.

```java
import com.nz1337.easysql.*;
import com.nz1337.easysql.manager.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.*;

public class Test extends JavaPlugin implements Listener {

    private Column column;

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.createDatabase();
    }

    private void createDatabase() {
        final Table table = new Table("breaked").setPrimaryKey("uuid").addColumn("uuid", "VARCHAR(36)").addColumn("blocks", "INTEGER");
        final EasySQL easySQL = new EasySQL();
        easySQL.setHost("127.0.0.1");
        easySQL.setPort(3306);
        easySQL.setUser("root");
        easySQL.setPassword("password");
        easySQL.setDatabase("server");
        easySQL.createDefaultTables(table);
        easySQL.connect();
        
        this.column = table.getColumns();
    }

    @EventHandler
    public void onConnect(final PlayerJoinEvent event) {
        final String uuid = event.getPlayer().getUniqueId().toString();
        this.column.insertDefault(uuid, 0);
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        final String uuid = event.getPlayer().getUniqueId().toString();
        final int oldBreakedBlocks = (int) this.column.getValue("uuid", uuid, "blocks");
        System.out.println("Old value for " + uuid + " : " + oldBreakedBlocks);
        
        this.column.editValue("uuid", uuid, "blocks", oldBreakedBlocks + 1);
        
        final int newbreakedBlocks = (int) this.column.getValue("uuid", uuid, "blocks");
        System.out.println("New value for " + uuid + " : " + newbreakedBlocks);
    }
}
```

**After use :**

<img src="https://cdn.discordapp.com/attachments/863095969436270633/873530143254142976/KECBABIkAE5hABSpBzqDPIFSJABIgAEZg7BChBzp2IEIABEgAkRgDhGgBDmHOoNcIQJEgAgQgblD4P8BkEXy4Qd6cMkAAAAASUVO.png"/>
<img src="https://cdn.discordapp.com/attachments/863095969436270633/873530616644243516/unknown.png"/>
