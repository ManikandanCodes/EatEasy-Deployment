import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {

  users: any[] = [];
  filteredUsers: any[] = [];
  loading = true;
  searchTerm: string = '';
  roleFilter: string = 'ALL';

  constructor(private adminService: AdminService) { }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getUsers().subscribe({
      next: (res: any) => {
        this.users = Array.isArray(res) ? res : [];
        this.filteredUsers = [...this.users];
        this.loading = false;
        this.filterUsers();
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.loading = false;
      }
    });
  }

  setRoleFilter(role: string) {
    this.roleFilter = role;
    this.filterUsers();
  }

  filterUsers() {
    let result = [...this.users];

  
    if (this.roleFilter !== 'ALL') {
      result = result.filter(u => u.role === this.roleFilter);
    }


    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(u =>
        u.name?.toLowerCase().includes(term) ||
        u.email?.toLowerCase().includes(term) ||
        u.phone?.toLowerCase().includes(term)
      );
    }

    this.filteredUsers = result;
  }

  getActiveUsersCount(): number {
    return this.users.filter(u => u.active).length;
  }

  getRoleLabel(role: string): string {
    const labels: any = {
      'CUSTOMER': 'Customer',
      'RESTAURANT_OWNER': 'Restaurant Owner',
      'ADMIN': 'Administrator',

    };
    return labels[role] || role;
  }

  blockUser(id: number) {
    this.adminService.blockUser(id).subscribe(() => {
      this.loadUsers();
    });
  }

  activateUser(id: number) {
    this.adminService.activateUser(id).subscribe(() => {
      this.loadUsers();
    });
  }


  selectedUser: any = null;

  viewDetails(user: any) {
    this.selectedUser = user;
  }

  closeDetails() {
    this.selectedUser = null;
  }
}
