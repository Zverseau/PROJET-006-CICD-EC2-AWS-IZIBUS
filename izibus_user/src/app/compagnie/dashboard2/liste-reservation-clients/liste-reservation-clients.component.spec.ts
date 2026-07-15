import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListeReservationClientsComponent } from './liste-reservation-clients.component';

describe('ListeReservationClientsComponent', () => {
  let component: ListeReservationClientsComponent;
  let fixture: ComponentFixture<ListeReservationClientsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListeReservationClientsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListeReservationClientsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
