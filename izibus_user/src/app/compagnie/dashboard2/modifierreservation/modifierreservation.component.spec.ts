import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifierreservationComponent } from './modifierreservation.component';

describe('ModifierreservationComponent', () => {
  let component: ModifierreservationComponent;
  let fixture: ComponentFixture<ModifierreservationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModifierreservationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifierreservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
