package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingRequestsActivity extends AppCompatActivity {

    private List<String> pendingUsers;  // List to store pending user emails

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_requests); // Updated layout file name


        // Set status and navigation bar colors
        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));

        // Reference to the container layout
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        // Fetch pending users from Firestore and populate the container layout
        fetchPendingUsers(containerLayout);
    }

    private void fetchPendingUsers(LinearLayout containerLayout) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pendingUsers = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String email = document.getString("email");
                            if (email != null) {
                                pendingUsers.add(email);
                            }
                        }

                        for (String email : pendingUsers) {
                            View cardView = LayoutInflater.from(this).inflate(R.layout.pending_request_item, containerLayout, false);

                            TextView emailTextView = cardView.findViewById(R.id.emailTextView);
                            emailTextView.setText(email);

                            MaterialButton acceptButton = cardView.findViewById(R.id.acceptButton);
                            acceptButton.setOnClickListener(v -> {
                            });

                            MaterialButton rejectButton = cardView.findViewById(R.id.rejectButton);
                            rejectButton.setOnClickListener(v -> {
                            });

                            containerLayout.addView(cardView);
                        }

                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }
        public void onAcceptClick(View view) {
            String documentId = (String) view.getTag();
            updateUserStatus(documentId, "accepted");
        }

        private void updateUserStatus(String documentId, String newStatus) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> updates = new HashMap<>();
            updates.put("status", newStatus);

            db.collection("users").document(documentId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("User status updated.");
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("ERROR - updating status");
                    });
    }
}


