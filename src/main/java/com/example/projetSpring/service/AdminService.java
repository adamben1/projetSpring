package com.example.projetSpring.service;

import com.example.projetSpring.model.Utilisateur;
import java.util.List;

public interface AdminService {
    List<Utilisateur> getAllUsers();
    List<Utilisateur> getAllAdmins();
    Utilisateur createUser(Utilisateur utilisateur);
    Utilisateur updateUser(Long id, Utilisateur utilisateur);
    void deleteUser(Long id);
    Utilisateur promoteToAdmin(Long id);
    Utilisateur demoteToUser(Long id);
    Utilisateur getUserByUsername(String username);
    void deleteAdmin(Long adminId);
}