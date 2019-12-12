let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();
//Firestore Trigger on Message create
exports.sendGroupMessage = functions.firestore
    .document('messages/{messageID}')
    .onCreate((snap, context) => {

      // Get an object representing the document
      const newValue = snap.data();
      //get Chat ID
      const chatid = newValue.chatid
      console.log("ChatID:", chatid);

    //Get UserIds from Chat
    let chatRef = db.collection('chats').doc(chatid);
	  let getChats = chatRef.get()
  		.then(doc => {
   			 if (!doc.exists) {
           throw new Error("No such chat with chat!");
    		} else {
      			//Get Array of UserIds
      			console.log("Userids:", doc.data().users);

            //get tokens from users--------------------------------
				    return db.collection('tokens').where('userid', 'in', doc.data().users).get()
          }
        })
 					 .then(snapshot => {
  						  if (snapshot.empty) {
                  throw new Error("No matching documents for UserID query.");
   							 }
                 let registrationTokens = new Array();

    						snapshot.forEach(doc => {
    							//Push Token in RegistrationToken Array
                  if(doc.data().token){
    							console.log("Token", doc.data().token);
    							registrationTokens.push(doc.data().token);
      						console.log(doc.id, '=>', doc.data());
                  }
    						});

    						//--Form Message Date--
                  let message;
    						//if message is event
      						if(newValue.event){

      							message = {
 		 							data: {
 		 								chatid: newValue.chatid,
 										creator: newValue.creator,
 										message: newValue.message,
 										isEvent: newValue.event,
 										timestamp : newValue.timeStamp,
 										id: newValue.id
 										//date: newValue.date
 										//status: newValue.status
 										//description: newValue.description
 									},
 		 								tokens: registrationTokens,
								}
      						}
      						else{
      							message = {
 									 data: {
 		 								chatid: newValue.chatid,
 										creator: newValue.creator,
 										message: newValue.message,
 										isEvent: newValue.event,
 										timestamp : newValue.timeStamp,
 										id: newValue.id
 									},
 										tokens: registrationTokens,
									}
      						}

     						//Send Message via Multicast
     						//???What if Tokens empty???
     						//Maybe make new Trigger on createToken for Users without Token

    						return admin.messaging().sendMulticast(message);
  					  })
              .then((response) => {
              if (response.failureCount > 0) {
                  const failedTokens = [];
                  response.responses.forEach((resp, idx) => {
                    if (!resp.success) {
                        failedTokens.push(registrationTokens[idx]);
                    }
                  });
                  console.log('List of tokens that caused failures: ' + failedTokens);
              }
              return console.log("Successfully sent message:", result);
              })
              .catch(err => {
                console.log('Error getting document', err);
              });
    		})
