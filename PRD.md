<<<<<<< HEAD
Act like an experienced developer and in the "app" project, 
	In the models  where id should always be GenerationType.IDENTITY
	=> request should be received in controllers and processed from services
	=> i want to add images folder that will keep uploaded images, so find the appropriate location for it
	=> Create these the models
		=> blog(id, tile(String),content(@Lob),status)
		=> comment(id(GenerationType.IDENTITY), title(string),content(@Lob), doneBy(long))
		=> picture(id, type, path, alt_text, dateTime, doneBy(long))
		=> category(id, name, done_by(long),doneBy(long))
		=> postComment(postId(long), commentId(long))
		=> postPictures(postId(long), picture(long))
		=> postCategory(postId(long), categoryId(long))
		=> post(id, dateTime,blogId, doneBy(long))
		=> likes(id, postId,dateTime,doneBy(long))
		=> FOREIGN KEYS
			=> postComment is a joint table for "post" and "comment", many to many
			=> postPictures is a joint table for "post" and pictures, many to many
			=> postCategory is a joint table for "post" and category, many to many
			=> likes has a one to many relationship with post
			=> blog has a one to many relationship with post.
		
	=> except for doneBy in  post, every other doneBy(long) is a normal field with long type, not a foreign key.
	=> CREATE CUSTOM EXCEPTION Class in a "exception package"
		=> it will be used in communication with frontend
	=> RESPONSES TO FRONTEND
		=> "200" should return "Successfully retrieved" plus the table name
		=> "401" should return "invalid credentials for login" and "unauthorized for other endpoints"
		=> "400" should return invalid inputs
	=> SWAGGER
		=> add @Operation with summary and description with suitable contents
	=> OTHER EXCEPTIONS
		=> Use try-catch in controllers
	=> REPOSITORIES
		=> Use projection interface for all repositories
		=> List<Object[]> should be used for dashboard responses only
	=> TEST
		=> TEST all endpoints one by one
		=> if a test fails check the root cause
		=> if all tests pass review the requirements one more time and let me know...
	-- have all tests been done on all endpoints, make sure you login, dont 
	-- can you login in the backend given username to be admin and password admin123
	
 
=======
like a pro software engineer and pro web dev,
	using the following tools: react-bootstrap to create some components where possible.
use all text stored in  a global file /data/texts.js with french/English translation
	 all text and call each text from it,and by making sure that the responsiveness stays operational
	make sure all text from <p> and all other html elements.
create a react/vite app, with javascript template
	 in this folder: D:\Apache\DEV\REACTJS\gestion_des_fichier, using pnpm
1. change the default homepage from App.jsx, change the default code to a four-pane
	menu grid.
	The grid has to have three panes, the panes should be wider than their
	height.
	the panes will act like a navigation of the system
2. the text of the panes should be as the following:
	LOCATION, USERS, DOCUMENT
3. use react-bootstrap to create some components where possible.
4. use light background all over the app.
5. use all text stored in  a global file /data/texts.js with french/English translation
	 all text and call each text from it,and by making sure that the responsiveness stays operational
	make sure all text from <p> and all other html elements.
6. make sure that the common styles are written in a global css file (.scss)
	here with reference of components tree, nest the styles like a pro
	usinig mixins for reusability
7. name of the website is GESTION DES FICHERS

8. for grid and responsiveness use the default react-bootstrap behavior and .scss
	for the custom css, so on homepage make sure the three panes are created in
	a Container-row-col kind of style.
9. Find a way to make the best reponsiveness for small devices.
10. make sure that all web app content are centered in a react-bootstrap container and 
	stucture its content using bootstrap style
11. the whole web layour should be full in 
 if you finish review the list one more time and let me know.
12. you fixed the global state from the title on top, but the layout for english is still not full
	when i click the language button the enlgish layout is coming not full 
13. i have changed the default language to en, but the header in App.jsx on line 14 is not full, please fix it
14. i saw the issue, the header expands based on the contents, so it should have display block
16. remove div from App.jsx  and header, just use React bootstra Container-Row-Col style
17. please redo the homepage by only making the header and use a simple React bootstra Container-Row-Col style,
	remove all header's custom css 
18. check if there are some bootsstrap customized styles and remove all of them
19. why is Row not gettin full with inside container in app.jsx on line 12
20. i have change the header to be on top, but it is in the middle, please fix it
21. i saw the issue was in index.css which had some styles that made everything in App.jsx to be vertically centered
	now it is ok, now do the following:
	- reapply the header a it way, 
	- reaapply the  three card, looped and use the global.scss where necessary, but dont customize bootstrap
	where not necessary. so the global .scss will again be imported in App.jsx
	- This time make the card icons horizontally placed on the left and the text on the right

1. create a spring boot app using the latest version which is suitable for java 17
	install the fillowing dependencies:
	<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
	   <dependency>
		   <groupId>org.mariadb.jdbc</groupId>
		   <artifactId>mariadb-java-client</artifactId>
		   <version>3.3.3</version>
	   </dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.datatype</groupId>
			<artifactId>jackson-datatype-hibernate6</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.30</version>
			<optional>true</optional>
		</dependency>
		<!-- JWT Dependencies -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.5</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>



2. note i have two projects inthe same workspace, i want to:
	- have all properties implemented in lawyer-backend to also be
	implemented in gestindesficher app
	- i also want to make sure all jwt authentication, swagger functionalities
	are implemented as they are implemented in lawyer-backend.
	- follow the same folder structure but instead of dtos use 
	project interfaces to be used in repositories
	- review the gestindesficher app and test if the swagger is working
	- The swagger is working perfectly but why isn't 'http://localhost:8104/api/auth/login'
	returning a jwt token ? is there another location where it is being returned?
	- now test the login and let me know if the token is being generated
3. i would like to group the current swagger endpoints to be grouped in a tag called "user"

4. also check if the feature of updating the password by user_id in accouts table,
5. check of there a functioality of retreiving all users. 
6. check of there is a functionality of viewing all users
7. i have now included an additional app in the workspace called gestion_des_ficher which is a frontend app that has to be integrated
	to the gestiondesfichier backend.
	- so start the integration by creating a login from frontend, all request will use axios so ceck of the package is installed
	- the requests should be in a folder from this path: src/services
	- by making the request setup in frontend make sure the reusability of the url path is present, for example the base path
		which corespond to the backend context path (Spring boot)
	- all the inserts should be in a file located here src/services/Inserts.jsx
	- All the get requests should be from src/services/GetRequests.jsx
	- all the update requests should be form src/services/UpdRequests.jsx
	- Start by login and make sure the token is maintained across all frontend components.
	- Change the frontend so that the login is the homepage
	- Make sure that all other componens are secure
	- Upon logging in, make sure the app redirects to the page that has the three panes located in App.jsx
	- Create the separate components for location another for user and another for document, where each component is called upon
		clicking its corresponding pane.
	- On each redirection, create a common menu that keep Location, user and document, so that the navigation os maintained
	- If all is set review this list and test one more time
	
8. as a pro java spring boot developer, Now i want you to create the necessary models, projection interfaces, repositories, services and 
	utils directories. find the common functionalities and make the necessary common classes in
	a package called "common"
	- However, now i want to use  a dedicated domain-based structure called "location", which will have its directories: models, projection interfaces, repositories, services
	- And another called document which will also its own models, projection interfaces, repositories, services
	- You will implement the the "location" based in the followiing entities:
		- country(id,name)
		- entity(id, name, countryId) where countryId is foreign key
		- module(id.name, entityId) where entityId is foreign key 
		- section (id,name, moduleId) where moduleId is foreign key 
	- know that the app is already running , so if all is done, review the list and test
	- if all tests pass, let me know
	
9. make the login pane a bit larger
10. in the frontend, make the title header a bit smaller in terms of font size and a little bit of the padding
11. Upon logging make the three tabs: location, user and location changed to a react-bootstrap navbar
	so the navbar will have submenu
	- the location will have country, entity, modules and sections
	-  the user will have account and roles
	- the document main menu will have some sub menus that i will tell you shortly.
12. make sure that eack of the submenu get its corresponding react component under /src/component,
	here each submenu will also have is folder under component.
	veryfy all each import if it is correctly imported correctly.
13. with reference to the backend database structure, make the corresponding forms in the frontend 
	"gestion_des_ficher"
	- for each form that has a foreign key field use a dropdown for that field, here check each table in the database
	- each form will use some insert request from services/
14. the component of each submenu should have a form that is structure like the models in the backend: com.bar.gestiondesfichier.location
	also you can refer to mcp database called:gestiondesfichier_db
15. now review the models fields from backend and make sure they align with frontend form fields
16. also create the necessary requests in frontend: /services/ to retrive data and load them in each submenu's component
17. i am getting errors on loading the data on each submenu
	on country Management i am getting an error alert: Failed to load countries: No static resource api/location/countries.
	on Entity Management i am getting an error alert: Failed to load entities: No static resource api/location/entities.
	on Module Management i am getting an error alert: Failed to load modules: No static resource api/location/modules.
	on Section Management Failed to load sections: No static resource api/location/sections.
18. also i want to replace  locationServices.jsx by Inserts.jsx, GetRequest.jsx, UpdRequests.jsx review all forms and change the 
	imports
19. now fix the request for user and its submenu as you fixed the ones from location from its submenu
9. make sure the design still follows the Container-Row-Col layout styles and redo the login
		-



-- on 18 september 2025 prompts:
-----------------------------------
- for each query in the repositories in the backend, make the pagination to be defaulted to 20 records per page, for this change the controller and 
	services accordingly
- fix the post request, i am getting this error: Failed to save country: Failed to create country while saving the country
- Observe the currect folder structure and keep the domain-based backend folder structure 
- Use a single package called "document" where it will hold models, repositories, controllers and services
- Use a common functionlity to be included in the common package found in com.bar.gestiondesfichier.common
- follow the logging of @slfj
- Return these https repsonses: 200 for a secceful request, 400 "check inputs",  403 "the your session has expired, please login again"
	- here make sure the frontend detects the response from backend and  alerts with the same backend message
	- those https reponses should be checked from frontend in all requests, post , get and put
- Follow the controller-service structure
- Use try and catch wherever possible in the services or controllers, here pick the best practice
- Keep using projection interfaces instead of dtos and make necessary changes in the repositories queries
- Document all endpoints for swagger
- Make a centralized @CrossOrigin tp allow frontend url, check if the "config" package exists and set the crossorigin up there
- in "config" package create  a 404 request not found config to return a specific message
	- for this, in frontend create common a response checker, which checks the returned response.
- 



In frontend i would like to:
	1. create a dashboard
	2. the system should redirect to the dashboard upon logging in.
	3. by referring to the backend database setup ,  check the database structure and create extensive dasboard with panes
	4. By following the existing backend domain-based structure and repositories queries, as pro crate queries that pulls reports of 	
		a file management system.
	5. make sure that the reports include files




In the database definition, i would like to add these models where:
	
	each controller will have pagination with default of 20 records.
	use the projection interfaces instead of dtos
	Make a centralized @CrossOrigin, 
	Use a single package called "document" where it will hold models, repositories, controllers and services
	Use a common functionlity to be included in the existing common package found in com.bar.gestiondesfichier.common
	follow the logging of @slfj
	Therefore,check the tables names that may conflict with mysql keywords create these models which will reflect the underying databse tables:
	=> docstatus             	(id, name),  the defaults are: applicable, suspended, remplaced, annulé, en_cours, acquis, vendu, transféré, litigieux, validé
	=> section_category   		(id, name), the default values are: financial,procument, hr, technical, IT, real Estate, Shareholders, legal, quality, HSE,	
		equipment, drug and alcohol, incident news letter, SOP
	=> norme_loi  			(id, date_time, doneby, docId, référence, description, date_vigueur, domaine_application, 												statut_id)
	=> comm_asset_land  		(id, date_time, doneby, docId, description, référence, date_btention, coordonnées GPS, emplacement, section_id, 									statut_id)
	=> permi_construction	  	(id, date_time, doneby, docId, référence_titre_foncier, réfé_permis_construire, date_validation, date_estimée_travaux, 									statut_id)
	=> accord_concession  		(id, date_time, doneby, docId, contrat_concession, emplacement, coordonnees_gps, rapport_transfert_gestion, date_debut_concession, date_fin_concession, 				statut_id)
	=> estate  			(id, date_time, doneby, docId, reference, estate_type,  emplacement, coordonnees_gps, date_of_building, comments, 									statut_id)
	=> equipemt_id  		(id, date_time, doneby, docId, equipment_type, serial_number, plate_number, etat_equipement, date_achat, date_visite_technique, assurance, documents_telecharger, 			statut_id)
	=> cert_licenses  		(id, date_time, doneby, docId, description, agent_certifica, numero_agent, date_certificate, duree_certificat, 										statut_id)
	=> comm_comp_policies 	 	(id, date_time, doneby, docId, reference, description, status, version, expiratino_date, sectionid, 											statut_id)
	=> comm_followup_audit  	(id, date_time, doneby, docId, reference, description, date_audit, auditor, num_non_conform, type_conform, percent_complete, doc_attach, section_id, 					statut_id)
	=> due_diligence  		(id, date_time, doneby, docId, reference, description, date_due_diligence, auditor, creation_date, completion_date, doc_attach, section_id, 						statut_id)
	=> comm_third_party  		(id, date_time, doneby, docId, name, location, validity, activities, section_id, 													statut_id)
	=> cargo_damage  		(id, date_time, doneby, docId, refe_request, description, quotation_contract_num, date_request, date_contract,		 								statut_id)
	=> litigation_followup  	(id, date_time, doneby, docId, creation_date, concern, statut, expected_completion, risk_value, 											statut_id)
	=> insurance  			(id, date_time, doneby, docId, concerns, coverage, values, date_validity, renewal_date, 												statut_id)
	=> third_party_claims 	 	(id, date_time, doneby, docId, reference, description, date_claim, department_in_charge, 												statut_id)

RELATIONSHIP
------------
- each table that has docid is in relationship with Document from com.bar.gestiondesfichier.document.model.Document.java
- each table that has statut_id is in relationship with docstatus
- i want to add expiration date in com.bar.gestiondesfichier.document.model.Document.java with LocalDateTime data_type and nullabe=false
- each table that has sectionid is in relationship with section_category table 

DEFAULT DATA
-------------
- in the load of the app, initialize the country for the model: com.bar.gestiondesfichier.location.model.Country, find the world list of countries online.
- for the load of the countries, check if the table is empty first to avoid duplicates



-- on 20 september 2025 prompts:
-----------------------------------
- i would like you tu check al the convertToProjection from gestiondesfichier in all controllers and check if they returned instance implements all 
	methods, if not please do it.
- now check if findByActiveTrueAndAuditTitleOrFindingsContaining exists in commFollowupAuditRepository
- now in CommThirdPartyController.java i have commThirdPartyRepository and i have called some methods in it, kindly check if ll the methods called in the controller
	are actually available.
- i would like to get rid of <Map<String, Object>> and use the the interafce projection inside AccordConcessionController.java on the getAllAccordConcession endpoint,
-  i have read that Map<String, Object> is not performant compared to the interface projection, in that refard i would like to use them for now.
	


>>>>>>> fd7535fcacbd8932e4a9a193d62c69650be7eafb

-- 22nd Sept 2025
------------------ ----------------------- -------------------------------------------------------------------------
i have this file EquipmentIdController.java and it has a non public class please adjust and follow these approach:
	public class named as its name
	make sure there is a single controller class inside it

i have this file EquipmentIdController.java and it has a non public class please adjust and follow these approach:
	public class named as its name
	make sure there is a single controller class inside it

i have this file BatchControllers2.java and it has a non public CommThirdPartyController class please adjust and follow these approach:
	public class named as its name
	make sure there is a single controller class inside it

i have this file BatchControllers3.java and it has a non public InsuranceController class and ThirdPartyClaimsController please a:
	split each controller into its own file (InsuranceController.java, ThirdPartyClaimsController.java


inside BatchControllers2.java i have CargoDamageController class and LitigationFollowupController class , i want to split each controller into its own file:
CargoDamageController.java , LitigationFollowupController.java

inside InsuranceController.java i have CommThirdPartyController alsn , i want to split each controller into its own file:
InsuranceController.java, LitigationFollowupController.java

s

<<<<<<< HEAD
i have this file EquipmentIdController.java and inside it there is this method:
 public ResponseEntity<Page<EquipmentIdProjection>> getAllEquipmentId(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "equipmentType") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Filter by section category ID") @RequestParam(required = false) Long sectionCategoryId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving equipment IDs - page: {}, size: {}, sort: {} {}, statusId: {}, search: {}", 
                    page, size, sort, direction, statusId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<EquipmentIdProjection> equipmentIds;
            
            if (search != null && !search.trim().isEmpty()) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndEquipmentTypeOrSerialNumberContainingProjections(search, pageable);
            } else if (statusId != null) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (sectionCategoryId != null) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndSectionCategoryIdProjections(sectionCategoryId, pageable);
            } else if (documentId != null) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
            } else {
                equipmentIds = equipmentIdRepository.findAllActiveProjections(pageable);
            }
            
            return ResponseEntity.ok(equipmentIds);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for equipment ID retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving equipment IDs", e);
            return ResponseEntity.badRequest().build();
        }
    }
it uses pagination, 
it filter using optional parameters, 
it has if statements to filter based on wether the parameters exist,
it uses the projection interface called	EquipmentIdProjection,
it does not need convertToProjection.
I want you to use the same approach in all controllers:

2. i have BatchControllers1.java and inside there are multiple controllers that have controllers: CommFollowupAuditController, DueDiligenceController,
	i want you to split each controller into its own file (CommFollowupAuditController.java, DueDiligenceController.java
=======
<<<<<<< HEAD
in the dashboard, in the left side menu, change the menu as collapsible by the main menu, so make sure the users can be folded in a toggle
	manner


  


As a pro frontend developer, using the same db structure as below:
		=> blog(id, tile(String),content(@Lob),status)
		=> tile(String),this will be a rte,status
		=> comment(id(GenerationType.IDENTITY), title(string),content(@Lob), doneBy(long))
		=> picture(id, type, path, alt_text, dateTime, doneBy(long))
		=> category(id, name, doneBy(long))
		=> postComment(postId(long), commentId(long))
		=> postPictures(postId(long), picture(long))
		=> postCategory(postId(long), categoryId(long))
		=> post(id, dateTime,blogId, doneBy(long))
		=> likes(id, postId,dateTime,doneBy(long))
and inline with the backend, do the necessary forms for each table in the dashboard:
	=> blog(tile ,content), the status will be automatic in the backend on the Blog model using @prepersist initialized as "Pending".
	for the content there has to be installed these tools:
	npm install @ckeditor/ckeditor5-react @ckeditor/ckeditor5-build-classic axios dompurify
	=> save picture(type, path, alt_text) where the path will be initialized by backend , so in the backend there should be a "images"
		 folder to hold them. Here the frontend should allow a user to browse picture on his local machine.
		 Also the front should be able to send the data along with multiple images, so the browse element should allow browsing 
		 multiple images. it would be better if the picture can be visualized before submitting.
	=> save the category(name) 
	=> save post, which automatically saves postCategory, and automatically saves postPictures to be saved from admin dashboard, on this 
		form the user wil select images from the server. those are the images save from the save picture form. the category will be 
		selected from a dropdown menu
	=> All the dateTime fields should be automatiacally be done from their corresponding backend models using @perpersist, so update the models 
		(picture, post,likes)
	=> All the doneBy should be come from frontend as the logged in user id, so check if the login holds the user id 
		in the session, but here use react-way

	=> MENU STRUCTURE:
		=> after user main menu, add "pictures" where this will have add edit and delete as submenu, "post categories" where this will
		also have add,edit and delete as submenu, "blog" as main menu and this has (add, edit and delete) as submenu
	=> Follow the list and implement the functionalities described.
	=> Once you finish, review one more time.

		


	Perfect, 
	=> Make the left side menu folded and leave post expanded by default
	=> Also, test all rerievals and make sure that all requests are not failing
	=> make the post go on top of other menus
	=> Check on the request being done on http://localhost:8080/api/blog, how it is bringing: 
		{"error":true,"message":"Internal server error","status":500}
	=> Make the left side menu scrollbar and make its container's height limited to the viewport of the whole page in 
		admin dashboard.
	=> on the Use the rich text editor  it is not appearing, there is appearing this message:
		Use the rich text editor to format your blog content. Status will be set to PENDING automatically.
	=> now the issue is that when i click on bold button it adds a tag like this: <strong>some contentt</strong>
		to all other buttons too i am not getting result live
	=> great, now make the left side menu have a fixed position and height so that it does scroll alogn
		 with other elements on the dashboard,but let other content to be scrollable
	=> also make a button to toggle the whole sidemenu hidden and show.
	=> The button that toggles is working but not working, now giving a white color
	=> on the richtext editor, give a fixed height too
	=> add a list in the toolbar also
	=> the two list buttons are added but not contents inside.



=======

	
>>>>>>> fd7535fcacbd8932e4a9a193d62c69650be7eafb
>>>>>>> 266b5e7cd35e791b19abb2bea4f73b8aa1cf5b46


Go on with CargoDamageController.java then CommThirdPartyController.java work on one by one 

In EstateController.java i have a class called Estate called in different methods but its methods like getPropertyName and getPropertyName are not found
	 getPropertyName, please check the structure and resoleve that.


in the backend app "gestion_ " CommThirdPartyRepository has some errors, please do this:
	1. check the model carefully
	2. check if the model exists and is caled in the repository properly and fix
	3. Check for the rest of the repositories as well
now check these errors:
- DueDiligenceRepository.java has error
- i am now getting << was unexpected at this time. when i try to clean build, plese check those symbols, by searching them and remove them
-  now when i compile i get this error:
Unexpected token ')' in expression or statement.
At line:8 char:6
+ @REM with the License.  You may obtain a copy of the License at
+      ~~~~
Unexpected token 'with' in expression or statement.
At line:10 char:9
+ @REM    https://www.apache.org/licenses/LICENSE-2.0
+         ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Unexpected token 'https://www.apache.org/licenses/LICENSE-2.0' in expression or statement.
Not all parse errors were reported.  Correct the reported errors and try again."
At line:1 char:83
+ ... 'mvnw.cmd'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Ra ...
+                 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    + CategoryInfo          : NotSpecified: (:) [], MethodInvocationException
    + FullyQualifiedErrorId : ParseException
 
Cannot start maven from wrapper  

there is an error in CommThirdPartyRepository because the usage of Page<CommThirdPartyProjection> stating that the is not public
 in com.bar.gestiondesfichier.document.projection; cannot be accessed from outside package, please do this:
- if the interfaces are inner classes, please make the public and created a dedicated public class fille in the same package 
- if not then check what is the issue.
- please check the CommFollowupAuditProjection.java it has non public intefaces inside,please do this:
	- check the usage of those non public  interfaces in the whole app and for each one create its public class file version in the
	same package.
	- fix these non public intefaces one by one
- please check the same inside CommFollowupAuditProjection.java there are some non-public interface classes
- By considering the use of base classes and parent classes, and by checking the inheritance used in backend, by considering that the models are in different 
	parts of the application, please, make a double check if the models create
	have the fields as the ones stated below:	
	=> docstatus             	(id, name),  the defaults are: applicable, suspended, remplaced, annulé, en_cours, acquis, vendu, transféré, litigieux, validé
	=> section_category   		(id, name), the default values are: financial,procument, hr, technical, IT, real Estate, Shareholders, legal, quality, HSE,	
		equipment, drug and alcohol, incident news letter, SOP
	=> norme_loi  			(id, date_time, doneby, docId, référence, description, date_vigueur, domaine_application, 												statut_id)
	=> comm_asset_land  		(id, date_time, doneby, docId, description, référence, date_btention, coordonnées GPS, emplacement, section_id, 									statut_id)
	=> permi_construction	  	(id, date_time, doneby, docId, référence_titre_foncier, réfé_permis_construire, date_validation, date_estimée_travaux, 									statut_id)
	=> accord_concession  		(id, date_time, doneby, docId, contrat_concession, emplacement, coordonnees_gps, rapport_transfert_gestion, date_debut_concession, date_fin_concession, 				statut_id)
	=> estate  			(id, date_time, doneby, docId, reference, estate_type,  emplacement, coordonnees_gps, date_of_building, comments, 									statut_id)
	=> equipemt_id  		(id, date_time, doneby, docId, equipment_type, serial_number, plate_number, etat_equipement, date_achat, date_visite_technique, assurance, documents_telecharger, 			statut_id)
	=> cert_licenses  		(id, date_time, doneby, docId, description, agent_certifica, numero_agent, date_certificate, duree_certificat, 										statut_id)
	=> comm_comp_policies 	 	(id, date_time, doneby, docId, reference, description, status, version, expiratino_date, sectionid, 											statut_id)
	=> comm_followup_audit  	(id, date_time, doneby, docId, reference, description, date_audit, auditor, num_non_conform, type_conform, percent_complete, doc_attach, section_id, 					statut_id)
	=> due_diligence  		(id, date_time, doneby, docId, reference, description, date_due_diligence, auditor, creation_date, completion_date, doc_attach, section_id, 						statut_id)
	=> comm_third_party  		(id, date_time, doneby, docId, name, location, validity, activities, section_id, 													statut_id)
	=> cargo_damage  		(id, date_time, doneby, docId, refe_request, description, quotation_contract_num, date_request, date_contract,		 								statut_id)
	=> litigation_followup  	(id, date_time, doneby, docId, creation_date, concern, statut, expected_completion, risk_value, 											statut_id)
	=> insurance  			(id, date_time, doneby, docId, concerns, coverage, values, date_validity, renewal_date, 												statut_id)
	=> third_party_claims 	 	(id, date_time, doneby, docId, reference, description, date_claim, department_in_charge, 												statut_id)

- check if findByActiveTrueAndSectionIdProjections is present in litigationFollowupRepository because it is bringing an error in LitigationFollowupController.java 
- By considering that the backend app has repositories in different locations and With the use of mcp, please check the description of each table in the database
	and check if each repository (one by one) is aligning with the table structure for native queries. remember to go one by one.








- as a frontend engineer, work on the document part. please do one requirement by one,and as you can see the backend has document-dedicated package that manages the document (controller, models/projections
	repository and services).
- so in the same way please make sure that te requests (post, get  and put) are inside src/components/services. those files are already there they are: GetRequests.jsx, Inserts.jsx, UpdRequests.jsx
- dont make another file for requests, just use those
- use the following tools: react-bootstrap to create some components where possible.
- use all text stored in  a global file /data/texts.js with french/English translation
	 all text and call each text from it
- Make the necessary endpoints based on the backend structure and then while making the data list table keep putting edit and delete buttons
- only if necessary do some css and in that case use global.scss
- and by making sure that the responsiveness stays operational
- refer to the existing forms setup and keep using modals for forms.
- each form will have its menu item under Document menu
- now as it is designed by domain in the backend i want you to create a folder called "document" inside /src/components/ and have all the forms for each of the below entities:
	=> docstatus             	(id, name),  the defaults are: applicable, suspended, remplaced, annulé, en_cours, acquis, vendu, transféré, litigieux, validé
	=> section_category   		(id, name), the default values are: financial,procument, hr, technical, IT, real Estate, Shareholders, legal, quality, HSE,	
		equipment, drug and alcohol, incident news letter, SOP
	=> norme_loi  			(id, date_time, doneby, docId, référence, description, date_vigueur, domaine_application, 												statut_id)
	=> comm_asset_land  		(id, date_time, doneby, docId, description, référence, date_btention, coordonnées GPS, emplacement, section_id, 									statut_id)
	=> permi_construction	  	(id, date_time, doneby, docId, référence_titre_foncier, réfé_permis_construire, date_validation, date_estimée_travaux, 									statut_id)
	=> accord_concession  		(id, date_time, doneby, docId, contrat_concession, emplacement, coordonnees_gps, rapport_transfert_gestion, date_debut_concession, date_fin_concession, 				statut_id)
	=> estate  			(id, date_time, doneby, docId, reference, estate_type,  emplacement, coordonnees_gps, date_of_building, comments, 									statut_id)
	=> equipemt_id  		(id, date_time, doneby, docId, equipment_type, serial_number, plate_number, etat_equipement, date_achat, date_visite_technique, assurance, documents_telecharger, 			statut_id)
	=> cert_licenses  		(id, date_time, doneby, docId, description, agent_certifica, numero_agent, date_certificate, duree_certificat, 										statut_id)
	=> comm_comp_policies 	 	(id, date_time, doneby, docId, reference, description, status, version, expiratino_date, sectionid, 											statut_id)
	=> comm_followup_audit  	(id, date_time, doneby, docId, reference, description, date_audit, auditor, num_non_conform, type_conform, percent_complete, doc_attach, section_id, 					statut_id)
	=> due_diligence  		(id, date_time, doneby, docId, reference, description, date_due_diligence, auditor, creation_date, completion_date, doc_attach, section_id, 						statut_id)
	=> comm_third_party  		(id, date_time, doneby, docId, name, location, validity, activities, section_id, 													statut_id)
	=> cargo_damage  		(id, date_time, doneby, docId, refe_request, description, quotation_contract_num, date_request, date_contract,		 								statut_id)
	=> litigation_followup  	(id, date_time, doneby, docId, creation_date, concern, statut, expected_completion, risk_value, 											statut_id)
	=> insurance  			(id, date_time, doneby, docId, concerns, coverage, values, date_validity, renewal_date, 												statut_id)
	=> third_party_claims 	 	(id, date_time, doneby, docId, reference, description, date_claim, department_in_charge, 												statut_id)


- a document will have the opening status called: "active" and the other to keep in a logical non editable location called "archived" and "expired", which should
	be alterted to admin before 2 weeks before the expiry.
- so in the backend on creating any document please put the default status as "active" using @prepersit, follow the structure of inheritance and find the 
	proper model or entity that holds te status.
- so do that to all models, but go one by one
- in frontend the post request should not supply the status as it will be defaulted in the backend, so in frontend check all forms and see only if the status is there and 
	remove if.
- after logging in, differentiate the users and prepare the dashboards, 
- by using mcp insert into status these: applicable, suspended, replaced, canceled, in progress, valid, rejetcted, rental, solid, free, expired
by using mcp, analyse the database where we want to track doc.

- in the backend make a @scheduled cron that runs every 30 minutes to check expirent component, follow the /document context-based design, find the suitable related existing controller or service
	to implement that 