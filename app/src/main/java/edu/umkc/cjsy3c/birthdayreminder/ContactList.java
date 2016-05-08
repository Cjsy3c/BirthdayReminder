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

public class ContactList {
    Context mContext;
    ArrayList<String> contacts;


    public ContactList(Context context){
        mContext = context;

    }

    /**
     * Gets a cursor of contact data. Gets contact name, and either birthday or anniversaries.
     * Eventtype does not matter if isEvent is false.
     *
     * @param eventType - Birthday or Anniversary.
     * @param isEvent   - Set false to fetch names only.
     * @return Cursor
     */
    public Cursor getContactCursor(boolean isEvent, int eventType) {
        // don't fetch anything but birthday or anniversary
        if ((eventType != ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY &&
                eventType != ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY)
                && isEvent) {   // if type is not birthday or anniversary && it is an event throw error
            new Error("Incorrect type in getContactCursor").printStackTrace();
        }

        // set uri
        Uri uri =  ContactsContract.Data.CONTENT_URI;

        String[] proj, args;

        String selection;

        if (isEvent) {
            // column names
            proj = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Event.START_DATE
            };

            // set selection
            selection = ContactsContract.Data.MIMETYPE +
                    "= ? AND " +
                    ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                    eventType;

            // set arguements
            args = new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        } else {
            // column names
            proj = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Event.CONTACT_ID
            };

            // set selection
            selection = "";

            // set arguements
            args = new String[]{};
        }

        //     getContentResolver().query(Uri, Projection, Selection, SelectionArgs, SortOrder);
        return mContext.getContentResolver().query(uri, proj, selection, args, null);
    }

    /**
     * Gets today's date and adds x number of days to that.
     * Returns the String format of the resulting date in "MM-dd" format.
     *
     * @param plus - Number of days to add to today's date
     * @return String of resulting date in MM-dd format
     */
    private String getDate(int plus){
        // return format is MM/dd
        DateFormat form = new SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        if (plus > 0)
            cal.add(Calendar.DATE, plus);

        return form.format(cal.getTime());
    }

    /** Sort the contact list, and limit the end result
     *  to the inputed time frame.
     *
     * @param timeFrame - Number of days to include
     */
    public void sortList(int timeFrame) {

        // assert error on null contacts and exit method
        if (contacts == null) {
            contacts = new ArrayList<>();
            new Error("Contacts not set in sortList call").printStackTrace();
            return;

        }
        // add today for sorting
        String today = getDate(0);
        contacts.add(today);
        ArrayList<String> newList = new ArrayList<>();

        if (timeFrame > 0) {    // limit to timeFrame days
            Long todayPOS = new Long(-1), endPOS = new Long(-1);

            // set end date for filter
            String end = getDate(timeFrame);// + " ZZZZZZZZZZZZZZZZZZZ";    // this changes whether this date is included
            contacts.add(end);
            Collections.sort(contacts); // sort

            // check for today and end.
            for (Long i = new Long(0); i < contacts.size(); i++) {
                if (contacts.get(i.intValue()).equals(today))
                    todayPOS = i + 1;
                else if (contacts.get(i.intValue()).equals(end))
                    endPOS = i;
            }
            if (todayPOS >= 0 && endPOS >= 0) {
                // return only results between those dates

                for (String s : contacts.subList(todayPOS.intValue(), endPOS.intValue()))
                    newList.add(s);

            }
        }
        else {
            // start at today in the list, loop the rest around
            Collections.sort(contacts);
            boolean done = false;
            Long count = new Long(0);

            while (!done) {
                // loop around until it shows today
                if (today.equals(contacts.get(count.intValue()))) {
                    done = true;
                } else {
                    contacts.add(contacts.get(count.intValue()));
                    count++;
                }
            }
            // add the ones beore today at the end of the list
            for (String s : contacts.subList((++count).intValue(), contacts.size()))
                newList.add(s);

        }

            contacts = newList;
    }

    /**
     * This method is just like findBirthdays except this lists all names.
     * This method doesn't include birthdates or anniversaries
     */
    public void findContacts() {
        Cursor cursor;
        cursor = getContactCursor(false, 0);
        contacts = new ArrayList<>();

        // fill contacts
        int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
        while (cursor.moveToNext()) {

            String name = cursor.getString(nameColumn);

            contacts.add(name);
        }
        cursor.close();

    }


    /**
     * This method fetches the contacts from the cursor and optionally fetches anniversaries also.
     *
     * @param timeFrame         - Limit the results to within timeFrame amount of days
     * @param ShowAnniversaries - Optionally show Anniversaries in the list
     */
    public void findBirthdays(int timeFrame, boolean ShowAnniversaries) {
        Cursor cursor;
        // Birthday List View
        try {
            cursor = getContactCursor(true, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
        } catch (NullPointerException e) {
            System.out.println("null pointer 1");
            return;
        }

        contacts = new ArrayList<>();


        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);

        while (cursor.moveToNext()) {

            String name = cursor.getString(nameColumn);
            StringBuilder bDay = new StringBuilder(cursor.getString(bDayColumn).trim());

            contacts.add(bDay.substring(5) + "      " + name);
        }
        cursor.close();

        if (ShowAnniversaries) {
            // add in TYPE_ANNIVERSARY
            try {
                cursor = getContactCursor(true, ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY);
            } catch (NullPointerException e) {
                System.out.println("Null pointer 2");
                return;
            }


            bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
            nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                StringBuilder bDay = new StringBuilder(cursor.getString(bDayColumn).trim());

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

    /**
     * This method should only be called after calling findBirthdays.
     * This method only returns the current list, calling findBirthdays updates the list.
     *
     * @return: List of contacts in string format
     */
    public ArrayList<String> getContacts() {
        if (contacts == null)
            contacts = new ArrayList<>();
        return contacts;
    }
}
