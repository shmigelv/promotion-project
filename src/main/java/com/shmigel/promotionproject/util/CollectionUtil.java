package com.shmigel.promotionproject.util;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtil {

    public static <T> Collection<T> toCollection(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

}
