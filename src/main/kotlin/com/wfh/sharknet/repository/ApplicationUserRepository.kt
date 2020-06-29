package com.wfh.sharknet.repository

import com.wfh.sharknet.model.ApplicationUser
import org.springframework.data.repository.PagingAndSortingRepository

interface ApplicationUserRepository : PagingAndSortingRepository<ApplicationUser?, Int> {
    fun findByUsername(username: String?): ApplicationUser?
}
