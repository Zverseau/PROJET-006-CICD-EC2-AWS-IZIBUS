import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompagnieTrajetsComponent } from './compagnie-trajets.component';

describe('CompagnieTrajetsComponent', () => {
  let component: CompagnieTrajetsComponent;
  let fixture: ComponentFixture<CompagnieTrajetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompagnieTrajetsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompagnieTrajetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
