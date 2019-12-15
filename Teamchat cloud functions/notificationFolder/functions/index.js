let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();

let registrationTokens = new Array();
let message;
//Firestore Trigger on Message create
exports.sendGroupMessage = functions
    .region('europe-west1')
    .firestore
    .document('messages/{messageID}')
    .onCreate((snap, context) => {

        // Get an object representing the document
        const newValue = snap.data();
        //get Chat id

        //Get UserIds from Chat
        let chatRef = db.collection('chats').doc(newValue.chatid);
        chatRef.get()
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

                snapshot.forEach(doc => {
                    //Push Token in RegistrationToken Array
                    if (doc.data().token) {
                        console.log("Token", doc.data().token);
                        registrationTokens.push(doc.data().token);
                        console.log(doc.id, '=>', doc.data());
                    }
                });

                //--Form Message Date--

                //if message is event TODO
                if (newValue.isEvent === "true") {
                    const message = {
                        notification: {
                            title: newValue.title,
                            body: newValue.message
                        },
                        data: {
                            id: context.params.messageID,
                            message: newValue.message,
                            chatid: newValue.chatid,
                            isEvent: newValue.isEvent,
                            creator: newValue.creator,
                            timestamp: newValue.timestamp,
                            date: newValue.date,
                            description: newValue.description,
                            status: newValue.status
                        },
                        tokens: registrationTokens,
                    }
                } else {
                    const message = {
                        notification: {
                            title: newValue.title,
                            body: newValue.message
                        },
                        data: {
                            id: context.params.messageID,
                            message: newValue.message,
                            chatid: newValue.chatid,
                            isEvent: newValue.isEvent,
                            creator: newValue.creator,
                            timestamp: newValue.timestamp
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
                return console.log("Successfully sent message:", response);
            })
            .catch(err => {
                console.log('Error getting document', err);
            });
    })
