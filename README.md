# World Food Distribution
The project consists in the development of an application that transmits interesting statistics regarding the amounts of produced,
imported or exported food products worldwide. The user is initially shown a
form providing him the possibility to set some parameters (e.g. he can select a
specific country or an entire region, a certain type of food, a time interval, the
analysis he is interested in performing and so on). The application computes the
requested analysis and displays the results through dashboards complete with
histograms, pie charts, totals and more: the user is enabled to do comparisons
between countries and to make further investigations. The app even provides
some interesting data on climate for the time interval selected. Moreover, the
system provides an eventual food production company with the possibility to
sign up and to add its new food data, enriching and updating the database.
Once a company is registered, the application others the possibility to modify
the enterprise account information, such as name, email, location, core business
(food produced) and so on.
On top of that, the application allows an administrator to sign in with his privileged credentials: the admin has to possibility to view the list of all registered
accounts, to remove a company account and to upload new data.

## Main actors
The main actors interacting with the application are the following:
1. User: an ordinary user who can exploit the features of the application
without having to sign in. He can set the parameters in order to see the
statistics relatively to the food, the country and the time interval he is
interested in.
2. Company: a company can sign up to the application providing some gen-
eral info such as company name, company location, email, core business
and so on. A company account is provided with the possibility to perform
the same actions of an ordinary user. On top of that, a company can enter
a dedicated area to update its new food production data and to update
its business information.
3. Administrator:the administrator can sign in to the system with his priv-
ileged credentials. The admin is provided with the possibility to manage
company accounts and, similarly to a company, he can upload new food
data to the application database.

## Use case
![Use case image](https://github.com/elenaveltroni/Task2/blob/master/UseCase3.jpg?raw=true)

## Software architecture
The application is based on a document database in MongoDB.
To manage the data stored in the database, the application uses the MongoDB
Java Drive that allows to interact with it employing Java Application.
![Software architecture image](https://github.com/elenaveltroni/Task2/blob/master/ArchitectureSchema.png?raw=true)
