import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavedItemComponent } from './saved-item.component';

describe('SavedItemComponent', () => {
  let component: SavedItemComponent;
  let fixture: ComponentFixture<SavedItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SavedItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavedItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
