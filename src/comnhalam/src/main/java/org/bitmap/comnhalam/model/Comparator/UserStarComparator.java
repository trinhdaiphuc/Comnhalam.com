package org.bitmap.comnhalam.model.Comparator;

import org.bitmap.comnhalam.model.User;

import java.util.Comparator;

public class UserStarComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        if(o1.getStar()>o2.getStar())
            return 1;
        if(o1.getStar()<o2.getStar())
            return -1;
        return 0;
    }
}
