# UMarketplace - Technical Report
UMarketplace is an app designed for users that want to exchange items via app. 

## V1 (UMarketplace) vs V1(Marketplace) - What's new
The previous version (v1) of this app included:

* Splash Screen
* Basic Authentication with Firebase
* JSON (for marketplace items)
* Add, Delete, Update items
* Map usage with location
* Image picker

For this v2 of the app we have: 
* MVVM: this is the design pattern applied for this version of the app. 
* Fragments: ListFragment includes all the items added by the users and can be filtered with a toggle. AddFragment to add a new item. MapFragment to view all the items in Google Maps, there is also the possibility to view each markes (for sale/sold items) for each individual or just the user items. AboutUs Fragment, includes info about the app. 
* NavDrawer: the NavDrawer contains the main functionality (add item, view list, view map, about us, signout)
* Menu: in app menu to show the main functionality available in each fragment
* Swipe Left/Right functionality: Swipe left to delete an item from the list, swipe right to edit an item. Swipe also left to view the NavBar menu. 
* Splash Screen: when the app starts, the splash screen will show the app logo for 3 seconds and then will leave space to a login screen. 
* Realtime DB Firebase and Firebase Storage
* Authentication with Firebase (password, email): at the login page, the user can decide to create a new account and login by using an email address and password, the credentials will be then stored in Firebase Authentication page
* Authentication with GoogleSignIn (via Firebase): at the login page, the user can use the option to login with a Google account, the credentials will be stored in Firebase Authentication page
* CRUD + Images: for an item creation, you can add an image, change it, delete it. The images are stored in Firebase Storage. 
* Map usage with location and custom markers: This functionality uses Google Maps API 

## UML & Class Diagrams
Package View created with CodeIris in Android Studio
[packageView](https://user-images.githubusercontent.com/22814086/147796695-12f0f4de-090d-46e6-a5ba-ea0dcd3487cc.png)


## UX/DX Approach Adopted
UX
* consistency of colours across the app, use of gradient colours for menu
* rounded icons and images create a better design 
* use of UI elements to enhance the user experience

DX
* NavDrawer functionality to navigate through fragments
* Good use of Helpers for images and swipe functionality
* MVVM with two-way data binding 

## Git approach adopted
I've worked on Android Studio with local commit (git add -A, git commit -m..), I've created a Develop Branch in Git but merged and pushed commit to main when ready.

## Personal Statement
I certify that this assignment/report is my own work, based on my personal study and/or research and that I have acknowledged all material and sources used in its preparation, whether they be books, articles, reports, lecture notes, and any other kind of document, electronic or personal communication.  
I also certify that this assignment/report has not previously been submitted for assessment in any other unit, except where specific permission has been granted from all unit coordinators involved, or at any other time in this unit, and that I have not copied in part or whole or otherwise plagiarised the work of other students and/or persons.

## References
List of the links I’ve consulted during the development
- Code and Lectures: https://next.tutors.dev/#/course/wit-hdip-comp-sci-2020-mobile-app-dev.netlify.app
- Google Custom Marker: https://developers.google.com/maps/documentation/android-sdk/marker#maps_android_markers_tag_sample-kotlin
- Fix bug (Stackoverflow): https://stackoverflow.com/questions/37949024/kotlin-typecastexception-null-cannot-be-cast-to-non-null-type-com-midsizemango
- Radio button: https://www.geeksforgeeks.org/radiobutton-in-kotlin/
- Kotlin - Shop demo: https://www.youtube.com/watch?v=hoK9OOP1KZw
- How to use Firebase Authentication in an Android MVVM App
https://www.youtube.com/watch?v=FuAz-ahdk0E
- Splash Screen: https://medium.com/@karenmartirosyan_64397/how-to-create-a-clean-splash-screen-with-mvvm-pattern-kotlin-coroutines-328e579f3524
- MVVM https://www.youtube.com/watch?v=d7UxPYxgBoA
https://www.youtube.com/watch?v=PPHkza3pZpU


