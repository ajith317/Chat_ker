package com.example.chat_ker.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chat_ker.R;
import com.example.chat_ker.databinding.ActivityMainBinding;
import com.example.chat_ker.menu.CallsFragment;
import com.example.chat_ker.menu.ChatsFragment;
import com.example.chat_ker.menu.StatusFragment;
import com.example.chat_ker.view.activites.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil. setContentView(this, R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

       setUpWithViewPager(binding.viewPager);
       binding.tabLayout.setupWithViewPager(binding.viewPager);
       setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changeFabIcon(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();

                        FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
                        if (fuser!= null ) {
                            Map<String,String> doc=new HashMap<>();
                            doc.put("token",token);
                            doc.put("id",fuser.getUid());
                            FirebaseFirestore.getInstance().collection("notification").document(fuser.getUid()).set(doc, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("Token:","AddedSuccessfully from msgService");
                                    }
                                    else{
                                        Log.d("Token:",task.getException().getMessage());
                                    }
                                }
                            });

                        }
                    }
                });
    }

    private void setUpWithViewPager(ViewPager viewPager){
        MainActivity.SectionsPageAdapter adapter=new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(),"Vathan");
        adapter.addFragment(new StatusFragment(),"Hinda Samachar");
        adapter.addFragment(new CallsFragment(),"Bov");
        viewPager.setAdapter(adapter);
    }
    private static class SectionsPageAdapter extends FragmentPagerAdapter{
        private final List<Fragment> fragmentList=new ArrayList<>();
        private final List<String> fragmentTitleList=new ArrayList<>();
        public SectionsPageAdapter(FragmentManager manager){
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.menu_search:
                Toast.makeText(this, "action search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_group:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_broacast:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_wa_web:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_starred_message:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_settings:
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;



        }
        return super.onOptionsItemSelected(item);
    }
  /*  @Override
    protected void onResume() {
        super.onResume();
        Map u = new HashMap();
        u.put("lastSeen", "online");
        db.collection("User").document(firebaseUser.getUid()).update(u);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateLastSeen();

    }

    @Override
    protected void onStop() {
        super.onStop();
        updateLastSeen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateLastSeen();
    }*/



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeFabIcon(final int index){
        /*binding.fabAction.hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (index){
                    case 0: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat_24));
                        binding.fabAction.setOnClickListener(  new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                            }
                        });
                      break;
                    case 1: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_photo_camera_24));break;
                    case 2: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_call_24));break;
                }
                binding.fabAction.show();
            }
        },400);*/

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("DESTROY","WINDOW");
        updateLastSeen();
    }

    public  void updateLastSeen(){
        if (firebaseUser !=null){
            Log.d("MESSAGE:","UPDATE LAST SEEN CALLED");
            Long tsLong = System.currentTimeMillis();
            //SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDateTime = dateFormat.format(tsLong+1000*60*60*5 + 1000*60*30);
            Map u = new HashMap();
            u.put("lastSeen",currentDateTime);
            db.collection("User").document(firebaseUser.getUid()).update(u);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Map u = new HashMap();
        u.put("lastSeen", "online");
        db.collection("User").document(firebaseUser.getUid()).update(u);
    }
}