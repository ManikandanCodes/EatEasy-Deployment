import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-management.component.html',
  styleUrls: ['./menu-management.component.css']
})
export class MenuManagementComponent {
  constructor(private router: Router) { }

  goToCategories() {
    this.router.navigate(['/owner/menu/categories']);
  }

  goToItems() {
    this.router.navigate(['/owner/menu/items']);
  }
}
