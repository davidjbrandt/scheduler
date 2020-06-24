# Scheduler
This is a JavaFX application I made for a school project. Its main purpose is to store customer and appointment information in a database for a hypothetical distributed consultancy company. It is designed to display date and time data in the user's local timezone and translate the user interface into the local language.
### A Note About the Database
Although this project was turned in with the ability to connect to a real MySQL database, those connection credentials are not included here for security purposes. The files as presented here are configured to use an "in-memory" database instead, which is able to do everything the real database can except the features involving data persistence.

Everything except the connection credentials is still present in the source code, and both versions use the same interface, so it would be relatively easy to supply credentials for another database and switch the production dependency back to the MySQL implementation.
### How to Login
The "in-memory" database simply checks whether the username is equal to the password. This operation is case-sensitive.
### Local Timezone and Language
Since the JVM sets the default Locale when it starts running, it is not possible to demonstrate the local timezone conversion feature of this application without connecting to a persistent database. However, if your machine's operating system language is set to a supported language before starting the application, it will automatically translate the user interface elements.
### Supported Languages
The application will default to English if the operating system is not set to any other supported language. Currently, the only other supported language is German, and the translations were done using Google Translate so they might not be exactly right.
### Adding Other Languages
The application is coded in such a way that no source code modification is necessary to add new languages. The \localization\resources folder contains a number of Resource Bundles that it uses for the translation process. To add another language, simply add a .properties file to each Bundle using the desired locale code(s) as a suffix, then copy one of the existing files and change anything following the = sign to match the target language.

For example, the "Alerts" Resource Bundle has Alerts.properties (the default) and Alerts_de.properties (de is German). If you want to add French, make a file called Alerts_fr.properties to the Alerts Bundle. Then copy the contents of one of the existing files (which take the form of key-value pairs such as "key=Value") and change the values to match the appropriate words/phrases in French. This is a Java standard and your IDE should have a way to simplify this process.