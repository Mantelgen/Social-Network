package com.example.socialnetworkgui.Repository.Database.Paging;

import com.example.socialnetworkgui.Domain.Entity;
import com.example.socialnetworkgui.Repository.Repository;

import com.example.socialnetworkgui.Utils.paging.Page;
import com.example.socialnetworkgui.Utils.paging.Pageable;


public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAllOnPage(Pageable pageable);
}
