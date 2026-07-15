import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompagnieReservationsComponent } from './compagnie-reservations.component';

describe('CompagnieReservationsComponent', () => {
  let component: CompagnieReservationsComponent;
  let fixture: ComponentFixture<CompagnieReservationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompagnieReservationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompagnieReservationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
