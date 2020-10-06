package org.bitmap.comnhalam.model.Comparator;

import org.bitmap.comnhalam.model.User;

import java.util.Comparator;

public class UserNameComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
