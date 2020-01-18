let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();


//Firestore Trigger on Message write
exports.sendGroupMessage = functions
    .region('europe-west1')
    .firestore
    .document('messages/{messageID}')
    .onWrite((change, context) => {
        let registrationTokens = new Array();

        // Get an object representing the document
        const newValue = change.after.data();
        if (newValue) {

        //get Chat id

        //Get UserIds from Chat
        let chatRef = db.collection('chats').doc(newValue.chatid);
        let chat=chatRef.get()
            .then(doc => {
                if (!doc.exists) {
                    throw new Error("No such chat with chat!");
                } else {
                    //Get Array of UserIds
                    ////console.log("Userids:", doc.data().users);

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
                        ////console.log("Token", doc.data().token);
                        registrationTokens.push(doc.data().token);
                        ////console.log(doc.id, '=>', doc.data());
                    }
                });

                //--Form Message--
                let message;
                ////console.log(newValue.isEvent);
                if (newValue.isEvent === 'true') {
                      message = {
                        data: {
                          title: newValue.message,
                          body: newValue.description,
                            id: context.params.messageID,
                            message: newValue.message,
                            chatid: newValue.chatid,
                            isEvent: newValue.isEvent,
                            creator: newValue.creator,
                            timestamp: newValue.timestamp,
                            date: newValue.date,
                            description: newValue.description,
                            status: newValue.status,
                            type: newValue.type,
                            action: newValue.action
                        },
                        tokens: registrationTokens,
                    }
                }
                else {
                      message = {

                        data: {
                          title: newValue.chatname,
                          body: newValue.message,
                            id: context.params.messageID,
                            message: newValue.message,
                            chatid: newValue.chatid,
                            type: newValue.type,
                            action: newValue.action,
                            isEvent: newValue.isEvent,
                            creator: newValue.creator,
                            timestamp: newValue.timestamp
                        },
                        tokens: registrationTokens,
                    }
                }
                ////console.log(message);

                //Send Message via Multicast
                //???What if Tokens empty???
                //Maybe make new Trigger on createToken for Users without Token
                const payload = message;
                return admin.messaging().sendMulticast(payload);
            })
            .then((response) => {
                if (response.failureCount > 0) {
                    const failedTokens = [];
                    response.responses.forEach((resp, idx) => {
                        if (!resp.success) {
                            failedTokens.push(registrationTokens[idx]);
                        }
                    });
                    //console.log('List of tokens that caused failures: ' + failedTokens);
                }
                //Delete Messaged
                //console.log("Successfully sent message:", response);
                //--Form Chat_notification--
                let message_noti
                if (newValue.isEvent === 'true') {
                  message_noti = {

                          notification: {
                            title: "[Event] "+newValue.message,
                            body: newValue.description,
                          },
                          tokens: registrationTokens,
                      }
                }
                else {
                  message_noti = {

                          notification: {
                            title: newValue.message,
                            body: newValue.description,
                          },
                          tokens: registrationTokens,
                      }
                }

                const payload_noti = message_noti;
                return admin.messaging().sendMulticast(payload_noti);
            })
            .then((response) => {
                if (response.failureCount > 0) {
                    const failedTokens = [];
                    response.responses.forEach((resp, idx) => {
                        if (!resp.success) {
                            failedTokens.push(registrationTokens[idx]);
                        }
                    });
                    //console.log('List of tokens that caused failures: ' + failedTokens);
                }
                //Delete Messaged
                return console.log("Successfully sent message_notification:", response);
            })
            .catch(err => {
                console.log('Error getting document', err);
            });
          }

    })


    //Firestore Trigger on Chats write
    exports.sendChat = functions
        .region('europe-west1')
        .firestore
        .document('chats/{chatID}')
        .onWrite((change, context) => {
            let registrationTokens = new Array();

            // Get an object representing the document
            const newValue = change.after.data();
            if (newValue) {

            //get Chat id

            //Get UserIds from Chat
              let tokens=db.collection('tokens').where('userid', 'in', newValue.users).get()
                .then(snapshot => {
                    if (snapshot.empty) {
                        throw new Error("No matching documents for UserID query.");
                    }


                    snapshot.forEach(doc => {
                        //Push Token in RegistrationToken Array
                        if (doc.data().token) {
                            //console.log("Token", doc.data().token);
                            registrationTokens.push(doc.data().token);
                            //console.log(doc.id, '=>', doc.data());
                        }
                    });

                    //--Form Chat--
                    let chat = {

                            data: {
                              title: newValue.name,
                              body: newValue.notification,
                                type: newValue.type,
                                action: newValue.action,
                                id: context.params.chatID,
                                admin: newValue.admin,
                                color: newValue.color,
                                name: newValue.name,
                                users: newValue.usersstring
                            },
                            tokens: registrationTokens,
                        }
                        //console.log(chat);
                    const payload = chat;
                    return admin.messaging().sendMulticast(payload);
                })
                .then((response) => {
                    if (response.failureCount > 0) {
                        const failedTokens = [];
                        response.responses.forEach((resp, idx) => {
                            if (!resp.success) {
                                failedTokens.push(registrationTokens[idx]);
                            }
                        });
                        //console.log('List of tokens that caused failures: ' + failedTokens);
                    }
                    //Delete Messaged
                    //console.log("Successfully sent Chat_data:", response);
                    //--Form Chat_notification--
                    let chat_noti = {

                            notification: {
                                title: newValue.name,
                                body: newValue.notification,
                            },
                            tokens: registrationTokens,
                        }
                    const payload_noti = chat_noti;
                    return admin.messaging().sendMulticast(payload_noti);
                })
                .then((response) => {
                    if (response.failureCount > 0) {
                        const failedTokens = [];
                        response.responses.forEach((resp, idx) => {
                            if (!resp.success) {
                                failedTokens.push(registrationTokens[idx]);
                            }
                        });
                        //console.log('List of tokens that caused failures: ' + failedTokens);
                    }
                    //Delete Messaged
                    return console.log("Successfully sent Chat_notification:", response);
                })
                .catch(err => {
                    console.log('Error getting document', err);
                });
              }

        })

        //Firestore Trigger on UserEvent write
        exports.sendusereventstatus = functions
            .region('europe-west1')
            .firestore
            .document('usereventstatus/{usereventstatusID}')
            .onWrite((change, context) => {
                let registrationTokens = new Array();
                // Get an object representing the document
                const newValue = change.after.data();
                if (newValue) {

                //get Chat id

                //Get UserIds from Chat
                let chatRef = db.collection('chats').doc(newValue.chatid);
                let chat=chatRef.get()
                    .then(doc => {
                        if (!doc.exists) {
                            throw new Error("No such chat with chat!");
                        } else {
                            //Get Array of UserIds
                            //console.log("Userids:", doc.data().users);

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
                                //console.log("Token", doc.data().token);
                                registrationTokens.push(doc.data().token);
                                //console.log(doc.id, '=>', doc.data());
                            }
                        });

                        //--Form UserEventStatus--
                        let usereventstatus = {

                                data: {
                                  title: newValue.eventname,
                                  body: newValue.notification,
                                    reason: newValue.reason,
                                    status: newValue.status,
                                    userid: newValue.userid,
                                    type: newValue.type,
                                    action: newValue.action,
                                    eventid: newValue.eventid
                                },
                                tokens: registrationTokens,
                            }
                            //console.log(usereventstatus);
                        const payload = usereventstatus;
                        return admin.messaging().sendMulticast(payload);
                    })
                    .then((response) => {
                        if (response.failureCount > 0) {
                            const failedTokens = [];
                            response.responses.forEach((resp, idx) => {
                                if (!resp.success) {
                                    failedTokens.push(registrationTokens[idx]);
                                }
                            });
                            //console.log('List of tokens that caused failures: ' + failedTokens);
                        }
                        //Delete Messaged
                        //console.log("Successfully sent UserEvent:", response);
                        //--Form Chat_notification--
                        let user_noti = {

                                notification: {
                                  title: "[Event update] "+newValue.eventname,
                                  body: newValue.notification,
                                },
                                tokens: registrationTokens,
                            }

                        const payload_noti = user_noti;
                        return admin.messaging().sendMulticast(payload_noti);
                    })
                    .then((response) => {
                        if (response.failureCount > 0) {
                            const failedTokens = [];
                            response.responses.forEach((resp, idx) => {
                                if (!resp.success) {
                                    failedTokens.push(registrationTokens[idx]);
                                }
                            });
                            //console.log('List of tokens that caused failures: ' + failedTokens);
                        }
                        //Delete Messaged
                        return console.log("Successfully sent UserEvent_notification:", response);
                    })
                    .catch(err => {
                        console.log('Error getting document', err);
                    });
                  }

            })
