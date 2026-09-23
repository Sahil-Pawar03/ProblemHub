package com.problemhub.service;

import com.problemhub.dao.BookmarkDAO;
import com.problemhub.model.Bookmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for student bookmarks.
 */
@Service
public class BookmarkService {

    private final BookmarkDAO bookmarkDAO;

    @Autowired
    public BookmarkService(BookmarkDAO bookmarkDAO) {
        this.bookmarkDAO = bookmarkDAO;
    }

    public boolean toggleBookmark(int userId, int problemId) {
        if (bookmarkDAO.isBookmarked(userId, problemId)) {
            bookmarkDAO.removeBookmark(userId, problemId);
            return false; // Now unbookmarked
        } else {
            bookmarkDAO.addBookmark(userId, problemId);
            return true; // Now bookmarked
        }
    }

    public boolean removeBookmark(int userId, int problemId) {
        return bookmarkDAO.removeBookmark(userId, problemId);
    }

    public boolean isBookmarked(int userId, int problemId) {
        return bookmarkDAO.isBookmarked(userId, problemId);
    }

    public List<Bookmark> getUserBookmarks(int userId) {
        return bookmarkDAO.getBookmarksByUserId(userId);
    }

    public int getBookmarkCount(int userId) {
        return bookmarkDAO.countByUserId(userId);
    }
}
