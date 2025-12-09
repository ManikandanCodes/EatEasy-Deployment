import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MenuService, Category } from '../../../../core/services/menu.service';
import { RestaurantService } from '../../../../core/services/restaurant.service';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  newName = '';
  editing: Category | null = null;
  editName = '';
  restaurantId: number | null = null;

  constructor(
    private menu: MenuService,
    private restService: RestaurantService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.restService.getMyRestaurant().subscribe({
      next: (res: any) => {
        if (res && res.id) {
          this.restaurantId = res.id;
          this.loadCategories();
        }
      },
      error: (err) => console.error('Failed to load restaurant', err)
    });
  }

  loadCategories() {
    if (!this.restaurantId) return;
    this.menu.getCategories(this.restaurantId).subscribe({
      next: (c) => (this.categories = c),
      error: (err) => console.error('Failed to load categories', err)
    });
  }

  addCategory() {
    if (!this.newName.trim() || !this.restaurantId) return;
    this.menu.addCategory({ name: this.newName, restaurantId: this.restaurantId }).subscribe({
      next: () => {
        this.newName = '';
        this.loadCategories();
      },
      error: (err) => console.error('Failed to add category', err)
    });
  }

  startEdit(cat: Category) {
    this.editing = cat;
    this.editName = cat.name;
  }

  saveEdit() {
    if (!this.editing) return;
    this.menu.updateCategory(this.editing.id, { name: this.editName }).subscribe({
      next: () => {
        this.editing = null;
        this.loadCategories();
      },
      error: (err) => console.error('Failed to update category', err)
    });
  }

  cancelEdit() {
    this.editing = null;
    this.editName = '';
  }

  deleteCategory(cat: Category) {
    if (confirm(`Delete category "${cat.name}"?`)) {
      this.menu.deleteCategory(cat.id).subscribe({
        next: () => this.loadCategories(),
        error: (err) => console.error('Failed to delete category', err)
      });
    }
  }

  goToItems(cat: Category) {
    this.router.navigate(['/owner/menu/items'], { queryParams: { categoryId: cat.id } });
  }
}
