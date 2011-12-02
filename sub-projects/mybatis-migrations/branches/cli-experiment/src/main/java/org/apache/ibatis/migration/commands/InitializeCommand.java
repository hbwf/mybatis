package org.apache.ibatis.migration.commands;

import java.io.File;
import java.util.Properties;

import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.MigrationsOptions;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Creates (if necessary) and initializes a migration path." )
public class InitializeCommand extends BaseCommand {

  public InitializeCommand(MigrationsOptions options)
  {
    super(options);
  }

  public void execute() {
    options.printStream.println("Initializing: " + options.basePath);

    createDirectoryIfNecessary(options.basePath);
    ensureDirectoryIsEmpty(options.basePath);

    createDirectoryIfNecessary(options.envPath);
    createDirectoryIfNecessary(options.scriptPath);
    createDirectoryIfNecessary(options.driverPath);

    copyResourceTo("org/apache/ibatis/migration/template_README", baseFile("README"));
    copyResourceTo("org/apache/ibatis/migration/template_environment.properties", environmentFile());
    copyResourceTo("org/apache/ibatis/migration/template_bootstrap.sql", scriptFile("bootstrap.sql"));
    copyResourceTo("org/apache/ibatis/migration/template_changelog.sql", scriptFile(getNextIDAsString() + "_create_changelog.sql"));
    copyResourceTo("org/apache/ibatis/migration/template_migration.sql", scriptFile(getNextIDAsString() + "_first_migration.sql"),
        new Properties() {
          {
            setProperty("description", "First migration.");
          }
        });
    options.printStream.println("Done!");
    options.printStream.println();
  }

  protected void ensureDirectoryIsEmpty(File path) {
    String[] list = path.list();
    if (list.length != 0) {
      for (String entry : list) {
        if (!entry.startsWith(".")) {
          throw new MigrationException("Directory must be empty (.svn etc allowed): " + path.getAbsolutePath());
        }
      }
    }
  }

  protected void createDirectoryIfNecessary(File path) {
    if (!path.exists()) {
      options.printStream.println("Creating: " + path.getName());
      if (!path.mkdirs()) {
        throw new MigrationException("Could not create directory path for an unknown reason. Make sure you have access to the directory.");
      }
    }
  }


}