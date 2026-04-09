import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleAuctionComponent } from './single-auction.component';

describe('SingleAuctionComponent', () => {
  let component: SingleAuctionComponent;
  let fixture: ComponentFixture<SingleAuctionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleAuctionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SingleAuctionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
