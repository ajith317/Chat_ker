package com.example.chat_ker.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chat_ker.R;
import com.example.chat_ker.databinding.ActivityMainBinding;
import com.example.chat_ker.menu.CallsFragment;
import com.example.chat_ker.menu.ChatsFragment;
import com.example.chat_ker.menu.StatusFragment;
import com.example.chat_ker.view.activites.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil. setContentView(this, R.layout.activity_main);

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
}