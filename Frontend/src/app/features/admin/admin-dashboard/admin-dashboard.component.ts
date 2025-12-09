import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  stats: any = null;
  loading = true;

  constructor(private adminService: AdminService) { }

  ngOnInit() {
    this.adminService.getAdminStats().subscribe({
      next: (res) => {
        this.stats = res;
        this.loading = false;
      }
    });
  }
}
