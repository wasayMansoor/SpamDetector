This Project is a spam detector that uses an unigram approach to filter out incoming spams by counting
every word and then associated with whether an email is spam or not.
The program can be trained using old emails that may or may not be spam and will calculate how accurate/precise
the current version is.
The accuracy and the precision are determined by the training data

The Base Projected has been upgraded by improving how the gui looks and added a directory chooser when training
or testing the emails.

To Run:
1) Compile the program using JavaFx and openJDK 12
2) Click train and select training data directory, training data should be split into spam and ham folders
3) Click test and select testing data directory
4) Emails should be displayed on the table and probability of it being spam, program will also display
how accurate/precise the current version of the program is