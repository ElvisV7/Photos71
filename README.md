This project is a JavaFX-based photo management application that lets users create, view, and manage photo albums with ease. The app supports multiple users (with an admin user for user management), photo uploads, album organization, tag-based photo searching, and a manual slideshow feature.

Features:
Login/Signup: Users can Log in with an existing Username or create a new account using the sign up button.

Admin Subsystem: The special username "admin" gives access to user management functions (list, create, and delets users).

Album Management: Users have personal albums that can be created, renamed, or deleted. Creating an album of images that show up in search is also possible.

Photo Management: Users can upload photos to albums, edit the details of those photos with captions and tags, add metadata to photos using tags, with tag types managed by utility files TagTypeManager.java and TagTypePersist.java.

Search Function: Users can search their photos by date range or tag criteria, but not both. When searching by two tags, if the operator value is not filled in with AND or OR, the search criteria will only be filled by the first tag input, and the second will be ignored. When search criteria is applied, the app can create a new album based on the results.

SlideShow Feature: A manual slideshow mode displays one photo at a time, with "Previous" and "Next" navigation buttons as well as automatic resizing. When reaching the end of an album, the next button will lead to the first photo in the album. When at the first photo of the album, the previous button will bring you to the last photo of the album.

Data Persistence: User data, along with albums and photo details, is saved using Java serialization and restored on application startup

Tags: Tags can be added or removed in the edit screen by selecting the tag type in a combo box and entering the tag value(case insensitive). If a tag type only allows one value and that value is filled, an error bubble will display to the user, and the tag will not be recorded. Users can create their tags by choosing other in the combo box and filling out the two input boxes for custom tag type, tag value, and the yes or no combo box for whether this type should allow multiple values. If in the Edit box, you choose other to make a new custom type and choose a tag type that already exists, a GUI popup will alert you that this type already exists and will not save. However, if you do this to a tag that already exists with only a singular selection allowed and set multiple to yes while adding a new value, it will provide a different error message and still not save your value. If you do this a custom type with multiple set to yes and you're trying to change it to no multiple, it will display this value takes one type error GUI and not save your new value however, the multiple property does not change and you can continue to add values the normal way after. Changes to the tags of a photo will not be implemented unless you press Done on the Edit window; closing it will result in your current actions not being saved. However, if you press Done on the edit screen and then close the application without logging out, all your tags will be saved.

Method of Installation for project:
1. Install the 2022-09 version of eclipse
2. Download the JavaFX SDK 21 version
3. Download JDK21
4. Install JDK21
5. In Eclipse marketplace, install e(fx)clipse
6. Once everything is installed, create the JavaFX library as a user library. To do this: Go to Window -> Preferences -> Java -> Build Path -> User Libraries

7.Click on New
8.Name it JavaFX
9. Once you add it, click on it, then click on Add External JARs. Then, look for the folder where you have the Java FX installed, go to the "lib" folder and select everything in there.
10. Check that all JARs are inside the JavaFX library
11. Click Apply and Close
12. Right-click on the project and go to Build Path -> Configure Build Path:
13. Go to Libraries
14. Add JavaFX (the library you just created)
15. Make sure JavaFX is in the Modulepath part
16. Double check that you have the correct JavaFX SDK and Java JDK21 in there, too.
17. Click Apply and Close
18. Right click on the project, go to Run As -> Run Configurations
19. Go to dependencies
20. Check that JavaFX (your library) is in the Module Path Entries section. If not, add it there.
21. To add any user library:
Go to Advanced -> Add Library, then click "OK" -> User Library, then click "Next" -> Select JavaFX -> Finish -> Click Apply
22. On the very same window in step 18, go to Arguments:
23. Add this on the VM arguments text box:
--module-path "THIS IS WHERE YOU PUT THE PATH TO THE JAVA FX SDK FOLDER" --add-modules javafx.controls,javafx.graphics,javafx.fxml
an example of the path:
--module-path "C:\Users\gehri\Downloads\openjfx-21.0.6_windows-x64_bin-sdk" --add-modules javafx.controls,javafx.graphics,javafx.fxml
24. Click Apply
25. Click Run
    
Following this step by step will allow you to run our project as we have been doing for the duration of its creation. We used JDK21.06 which is a version of JDK21 as stipulated by the project requirements and Java FX SDK 21, also stipulated. Note that in order for the project to use these Java implements you must be running Eclipse IDE 2022-09 or an earlier version, all newer versions of eclipse would not allow us to use Java FX. We also made use of e(fx)clipse from the Eclipse marketplace. 
