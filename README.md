# DocAI

The purpose of this work is to provide a platform for medical data sharing in the healthcare industry using blockchain technology. Patients, medical staff, medical institution's admin and system admin are possible roles in the network. It is a patient-centric system which means that patients are owner's of all their examinations. They have full rights to arrange who can access his/her documents and under which conditions.

Basic functionalities of the application are:
* User registration
* Displaying and editing profile information
* Searching medical documents and requesting access to a particular one
* Submiting clinical examination via medical staff
* Viewing patient's examination
* Defining access rights for patient's own health documents
* Making decision upon examination access request


## Configuration setup
Hyperledger Fabric is used for blockchain network configuration. For installing Fabric and all necessary programs visit [prerequisites](https://hyperledger-fabric.readthedocs.io/en/latest/prereqs.html) and [installation process](https://hyperledger-fabric.readthedocs.io/en/latest/install.html). Latest version
of Hyperledger Fabric is implemented in this project. The configuration and organization of the network is similar to Fabric [test network](https://hyperledger-fabric.readthedocs.io/en/latest/test_network.html).

In addition, you need to install Java, MySql and MongoDB in order to properly run backend applications in the project. Afterwards, you have to adapt database connection parameters at your values.

Script [**start.sh**](https://github.com/jstamps91/DocAI/blob/main/start.sh) is a shorthand for starting all applications and if every module is corectly setup, DocAI platform will be launched.
