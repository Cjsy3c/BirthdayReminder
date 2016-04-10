package edu.umkc.cjsy3c.birthdayreminder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by Cody on 3/16/2016.
 */
public class ContactList {
    Context mContext;
    ArrayList<String> contacts;


    public ContactList(Context context){
        mContext = context;
        contacts = new ArrayList<String>();

    }

    public Cursor getContactCursor(int eventType)
    {
        // set uri
        Uri uri =  ContactsContract.Data.CONTENT_URI;
        String[] proj = {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE
        };
        // set selection
        String selection = ContactsContract.Data.MIMETYPE +
                "= ? AND " +
                ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                eventType;

        // set arguements
        String[] args = {ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};

        //     getContentResolver().query(Uri, Projection, Selection, SelectionArgs, SortOrder);
        return mContext.getContentResolver().query(uri, proj, selection, args, null);
    }

    private String getDate(int plus){
        // return format is MM/dd
        DateFormat form = new SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        if (plus > 0)
            cal.add(Calendar.DATE, plus);

        return form.format(cal.getTime());
    }

    public void sortList(int timeFrame) {
        // sort and limit by date

        String today = getDate(0);
        contacts.add(today);
        ArrayList<String> newList = new ArrayList<String>();

        if (timeFrame > 0) {
            int todayPOS =-1, endPOS =-1;

            String end = getDate(timeFrame);// + " ZZZZZZZZZZZZZZZZZZZ";
            contacts.add(end);
            Collections.sort(contacts);
            for (int i =0; i < contacts.size(); i++)
            {
                if (contacts.get(i).equals(today))
                    todayPOS = i+1;
                else if (contacts.get(i).equals(end))
                    endPOS = i;
            }
            if (todayPOS >= 0 && endPOS >= 0) {


                for(String s : contacts.subList(todayPOS, endPOS))
                    newList.add(s);

            }
        }
        else {
            // start at today in the list, loop the rest around
            Collections.sort(contacts);
            boolean done = false;
            int count = 0;

            while (!done) {

                if (today.equals(contacts.get(count))) {
                    done = true;
                } else {
                    contacts.add(contacts.get(count));
                    count++;
                }
            }

            for (String s : contacts.subList(++count, contacts.size()))
                newList.add(s);

        }

            contacts = newList;
    }


    public void findBirthdays(int timeFrame, boolean ShowAnniversaries) {

        // Birthday List View
        Cursor cursor = getContactCursor(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
        // add in TYPE_ANNIVERSARY
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameColumn);
            StringBuffer bDay = new StringBuffer(cursor.getString(bDayColumn).trim());

            contacts.add(bDay.substring(5) + "      " + name);
        }
        cursor.close();

        if (ShowAnniversaries) {
            // add in TYPE_ANNIVERSARY
            cursor = getContactCursor(ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY);

            bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
            nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                StringBuffer bDay = new StringBuffer(cursor.getString(bDayColumn).trim());

                contacts.add(bDay.substring(5) + "      " + name + "'s Anniversary");
            }
            cursor.close();
        }

        if (contacts.size() == 0)
            contacts.add("No Birthdays Found");
        else {
            sortList(timeFrame);

            if (contacts.size() == 0) {
                contacts.add("No Birthdays found in the next " + timeFrame + " days");
            }
        }

    }

    public ArrayList<String> getContacts(){return contacts;}
}
