package com.zubisofts.menuqrgenerator.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;
import com.zubisofts.menuqrgenerator.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataRepository {

    private static final String TAG = "DataRepository";

    private static FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static void saveUser(final User user, final MutableLiveData<Response> userLiveData) {
        mDb.collection("users")
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userLiveData.postValue(new Response(
                                false,
                                user
                        ));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userLiveData.postValue(new Response(
                                true,
                                e.getMessage()
                        ));
                    }
                });
    }

    public static void getUser(final String uid, final MutableLiveData<Response> userLiveData) {
        mDb.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.postValue(new Response(
                                false,
                                user
                        ));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userLiveData.postValue(new Response(
                                true,
                                e.getMessage()
                        ));
                    }
                });
    }

    public static void fetchRestaurantMenu(final String resId, final MutableLiveData<Response> menuResponse) {
        mDb.collection("menus")
                .document(resId)
                .collection("data")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        ArrayList<FoodMenuItem> foodMenuItems = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            foodMenuItems.add(doc.toObject(FoodMenuItem.class));
                        }

                        menuResponse.postValue(new Response(
                                false,
                                foodMenuItems
                        ));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                menuResponse.postValue(new Response(
                        true,
                        e.getMessage()
                ));
            }
        });
    }

    public static void uploadRestaurantImage(final String uid, final Restaurant restaurant, final DocumentReference documentReference, final MutableLiveData<Response> menuLiveData) {

        final StorageReference storageReference = storage.getReference()
                .child("images")
                .child(uid)
                .child(restaurant.getId())
                .child(Uri.parse(restaurant.getIconUrl()).getLastPathSegment());

        UploadTask uploadTask = storageReference.putFile(Uri.parse(restaurant.getIconUrl()));

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    documentReference.update("iconUrl", String.valueOf(downloadUri));
                    menuLiveData.postValue(new Response(
                            false,
                            downloadUri
                    ));
                } else {
                    // Handle successful uploads
                    menuLiveData.postValue(new Response(
                            true,
                            task.getException().getMessage()
                    ));
                }
            }
        });
    }

    public static void saveRestaurantDetails(final Restaurant restaurant, final MutableLiveData<Response> menuLiveData) {
        final DocumentReference documentRef = mDb.collection("restaurants")
                .document(restaurant.getOwnerId())
                .collection("data")
                .document();

        restaurant.setId(documentRef.getId());
        documentRef.set(restaurant).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                saveMenuItems(restaurant.getOwnerId(), restaurant, documentRef, menuLiveData, true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                menuLiveData.postValue(new Response(
                        true,
                        e.getMessage()
                ));
            }
        });


    }

    public static void updateRestaurantDetails(final String uid, final Restaurant restaurant, final MutableLiveData<Response> menuLiveData, boolean includeLogo) {
        final DocumentReference documentRef = mDb.collection("restaurants")
                .document(uid)
                .collection("data")
                .document(restaurant.getId());

        documentRef.set(restaurant).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                saveMenuItems(uid, restaurant, documentRef, menuLiveData, includeLogo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                menuLiveData.postValue(new Response(
                        true,
                        e.getMessage()
                ));
            }
        });


    }

    public static void saveMenuItems(final String uid, final Restaurant res, final DocumentReference documentReference, final MutableLiveData<Response> menuLiveData, boolean includeLogo) {

        WriteBatch writeBatch = mDb.batch();
        for (FoodMenuItem foodMenuItem : res.getMenuItems()) {
            Log.i(TAG, "saveMenuItems: ID:" + foodMenuItem.getTimestamp());
            DocumentReference docRef;
            if (foodMenuItem.getId().equals("")) {
                docRef = mDb.collection("menus")
                        .document(res.getId())
                        .collection("data")
                        .document();
                foodMenuItem.setId(docRef.getId());
                writeBatch.set(docRef, foodMenuItem);
            } else {
                docRef = mDb.collection("menus")
                        .document(res.getId())
                        .collection("data")
                        .document(foodMenuItem.getId());

                Map<String, Object> data = new HashMap<>();
                data.put("id", foodMenuItem.getId());
                data.put("name", foodMenuItem.getName());
                data.put("description", foodMenuItem.getDescription());
                data.put("category", foodMenuItem.getCategory());
                data.put("price", foodMenuItem.getPrice());
                data.put("timestamp", foodMenuItem.getTimestamp());

                writeBatch.update(docRef, data);
            }

        }

        // Commit the batch
        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (includeLogo) {
                    uploadRestaurantImage(uid, res, documentReference, menuLiveData);
                } else {
                    menuLiveData.postValue(new Response(
                            false,
                            res
                    ));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                menuLiveData.postValue(new Response(
                        true,
                        e.getMessage()
                ));
            }
        });

    }

    public static void fetchRestaurantMenus(String uid, final MutableLiveData<Response> restaurantListResponse) {

        mDb.collection("restaurants")
                .document(uid)
                .collection("data")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        ArrayList<Restaurant> list = new ArrayList<>();
                        if (error == null) {
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                list.add(doc.toObject(Restaurant.class));
                            }

                            restaurantListResponse.postValue(new Response(
                                    false,
                                    list
                            ));
                        }

                    }
                });

    }

    public static void deleteRestaurantMenus(String uid, final String resId) {

        mDb.collection("restaurants")
                .document(uid)
                .collection("data")
                .document(resId)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public static void checkAndSaveUserToDB(User user, MutableLiveData<Response> userResponse) {

        mDb.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()){
                            saveUser(user,userResponse);
                        }
                    }
                });

    }
}
