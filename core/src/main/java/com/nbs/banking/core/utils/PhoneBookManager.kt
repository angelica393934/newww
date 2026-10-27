package com.nbs.banking.core.utils

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import com.nbs.banking.core.extensions.emptyString
import com.nbs.banking.core.extensions.revertPhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PhoneBookManager @Inject constructor(private val contentResolver: ContentResolver) {
    private fun getPhoneContacts(): ArrayList<PhoneBook> {
        val contactsList = ArrayList<PhoneBook>()
        val contactsCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC",
        )
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(PhoneBook(id = id, name = name))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    private fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null,
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                // check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            // contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    private fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            null,
            null,
            null,
        )
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                // check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId]?.add(email)
                } else {
                    contactsEmailMap[contactId] = arrayListOf(email)
                }
            }
            // contact contains all the emails of a particular contact
            emailCursor.close()
        }
        return contactsEmailMap
    }

    suspend fun fetchContacts(
        useFilter: Boolean = false,
        filterParam: FilterContactParam = FilterContactParam(),
        withEmail: Boolean = true,
    ): MutableList<PhoneBookContact> = withContext(Dispatchers.IO) {
        val phoneBookContacts = mutableListOf<PhoneBookContact>()
        val phoneBookContactsMapping = mutableListOf<PhoneBookContact>()
        try {
            val contactsListAsync = async { getPhoneContacts() }
            val contactNumbersAsync = async { getContactNumbers() }
            val contactEmailAsync = async { getContactEmails() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()
            val contactEmails = contactEmailAsync.await()

            contacts.forEach { contact ->
                contactNumbers[contact.id]?.let { numbers ->
                    contact.numbers = numbers.map { it.filter { char -> char.isDigit() } }.toSet().toList()
                }
                contactEmails[contact.id]?.let {
                    contact.emails = it
                }
            }

            contacts.forEach { phoneBook ->
                phoneBook.numbers.forEach { number ->
                    phoneBookContactsMapping.add(
                        PhoneBookContact(
                            id = phoneBook.id,
                            name = if (phoneBook.name.revertPhoneNumber() == number.revertPhoneNumber()) emptyString() else phoneBook.name,
                            phoneNumber = number,
                        ),
                    )
                }

                if (withEmail) {
                    phoneBook.emails.forEach { email ->
                        phoneBookContactsMapping.add(
                            PhoneBookContact(
                                id = phoneBook.id,
                                name = phoneBook.name,
                                email = email,
                            ),
                        )
                    }
                }
            }

            if (useFilter) {
                val filteredContacts = filterItemsByContains(phoneBookContactsMapping, filterParam)
                phoneBookContacts.addAll(filteredContacts)
            } else {
                phoneBookContacts.addAll(phoneBookContactsMapping)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("getAllContacts").e("Error querying contacts: ${e.message}")
        }

        phoneBookContacts.sortBy { it.name.lowercase() } // Sort by contact name
        phoneBookContacts
    }
}

fun filterItemsByContains(items: List<PhoneBookContact>, filterParam: FilterContactParam): List<PhoneBookContact> = if (filterParam.filterType == FilterContactTypeEnum.CONTAINS) {
    items.filter { item ->
        filterParam.queryAllowed.any { filter -> item.phoneNumber.contains(filter, ignoreCase = true) }
    }
} else {
    items.filter { item ->
        filterParam.queryAllowed.any { filter -> item.phoneNumber.startsWith(filter, ignoreCase = true) || (item.email.isNotEmpty() && filterParam.withEmail) }
    }
}

data class PhoneBookContact(
    val id: String = emptyString(),
    val name: String = emptyString(),
    var phoneNumber: String = emptyString(),
    var email: String = emptyString(),
)

data class PhoneBookGroup(
    val initialName: String,
    var items: List<PhoneBookContact> = emptyList(),
)

data class PhoneBookUnGroup(
    val initialName: String,
    var item: PhoneBookContact = PhoneBookContact(),
)

data class PhoneBook(
    val id: String = emptyString(),
    val name: String,
    var numbers: List<String> = emptyList(),
    var emails: List<String> = emptyList(),
)

enum class FilterContactTypeEnum {
    CONTAINS,
    START_WITH,
}

data class FilterContactParam(
    val queryAllowed: List<String> = emptyList(),
    val withEmail: Boolean = false,
    val filterType: FilterContactTypeEnum = FilterContactTypeEnum.START_WITH,
)
