# Chat Bot
This repository contains a chat bot scripting language that is simple to use. This software was developed to introduce kids ages 8~15 to some concepts in computer science and natural language processing.

#Running the bot
To run the program you need to execute the JAR file. This means that you need to open a command window. On a Windows operating system you can hit the Windows+R keys, type "cmd", and hit Enter. This will open a command prompt window. There are many tutorials online that will show you how to use this command prompt but two basic commands you need to know are "cd" and "dir". Dir will list the current contents of a directory you are in. These are the same files you would see if you opened the folder using Windows' graphical interface. Cd will change the directory you are in. To go up a directory you just have to type "..". To navigate to a deeper folder, you just type its name. If the name has a space in it you have to use double quotes around the name when you are typing it in the command prompt. You can use these two commands to navigate to the directory that contains the chat bot code. When you are in the directory that contains ChatDemo.jar you must type "java -jar ChatDemo.jar" to run the program. If it tells you it does not recognize the program "java" this means Java isn't in your system path. There are again, many tutorials online to configure your system path variable.

#Instructions

Dialog systems (or chat bots) are computer systems you can talk to. This could be for educational or medical purposes, or just for fun!

In this demo you will have a chat window and a text editor open. The text editor will have rules written that look like this:

r:(hello) Hi!

These are rules you can edit to change how your bot works. This rule says that when it receives the input “hello” it should respond “Hi!”. The input is in parenthesis and the output comes after it. To try this out you can type hello into your chat window. You should see the bot responds with a greeting.

Sometimes you will want your bot to randomly select between a few different responses. The first rule in your file will be this:

r:(hello) Hi!|Hello|How are you doing?

The vertical bar (hit SHIFT + the key between backspace and enter) will tell the bot to separate the responses so it will randomly choose one of the three when it responds. It will say either “Hi!”, “Hello”, or “How are you doing?”

There are two types of rules in your file, you will see some that start with “r:” and some that start with “g:”. The “r:” rules are response rules. These are only said when the input in parenthesis matches what you type. The bot will respond with the text on the right.

The second type of rule is called a gambit. These rules start with “g:” and are used when the chatbot has no matching responses. You can use these to make your bot seem like it has its own interests and subjects it wants to talk about.

If you want to match responses to something your bot says you can write rules indented with a TAB character underneath your rule. For example:

g:Whats up?
    r:(nothing) cool
    r:(nothing much) same here
    
This will match “nothing” as input if you respond with this when the bot asks “What’s up?” and respond “cool”.

One final thing you can try is using the wildcard character ‘*’. If you add this to a response it means that it will match any number of characters. For example:

r:(i like *) me too!

Is a rule that says if you type “I like” followed by anything it will match this rule and respond “me too!”

Try and edit the bot! You can write in the text editor and save the file. Then type “!build” in the chat window. It should tell you that the bot has been rebuilt and your instructions will now be loaded.
