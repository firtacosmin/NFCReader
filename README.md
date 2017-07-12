# NFCReader
The application that will read the nfc data from the TAG

This application is build to read the data received via NFC from the NFCTag appliction.
It will display a blue splash screen for one minute every time it returnes from background.
It will read the nfc information and will take actions depending on the payload of the received message:
  - if a "nothing" payload is received it will print a SnackBar with the message "Nothing received"
  - if an "unlock" payload is received the application will print a gif animation, will enable the drawer and wll display 
  user information into the drawer. The user has the posibiliti to press the "Logout" button and the app will go into main screen, blocking the drawer and displaying a message

The NFC reception is done via Intent update. From the new intent the NdefRecords are read and from there will get the payload byte array. 
The received byte array will be transformed into string and will be tested against the predefined string values, "nothing","unlock".
