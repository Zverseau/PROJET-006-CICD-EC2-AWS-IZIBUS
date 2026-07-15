import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifiertrajetComponent } from './modifiertrajet.component';

describe('ModifiertrajetComponent', () => {
  let component: ModifiertrajetComponent;
  let fixture: ComponentFixture<ModifiertrajetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModifiertrajetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifiertrajetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
