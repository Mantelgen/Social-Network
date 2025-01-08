package com.example.socialnetworkgui.Repository.Database.Paging;



import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Utils.paging.Page;
import com.example.socialnetworkgui.Utils.paging.Pageable;


import java.util.List;

public interface FriendshipRepository extends PagingRepository<Tuple<Long,Long>,Friendship> {
    List<Integer> getYears();

    Page<Friendship> findAllOnPage(Pageable pageable, FriendshipDTO movieFilter);
}
